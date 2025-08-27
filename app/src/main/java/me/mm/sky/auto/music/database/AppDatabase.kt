package me.mm.sky.auto.music.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.content.edit

@Database(entities = [Song::class], version = 2) // 版本号从1升级到2
@TypeConverters(SongNotesConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val PREF_NAME = "app_database_prefs"
        private const val KEY_COMPRESSION_MIGRATED = "compression_migrated"

        // 数据库迁移：将现有未压缩数据转换为压缩格式
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.d("AppDatabase", "开始数据迁移：准备压缩现有 songNotes 数据")

                try {
                    // 创建新表，songNotes 和 updater 字段设为可空
                    db.execSQL("""
                CREATE TABLE IF NOT EXISTS songs_new (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    author TEXT NOT NULL,
                    transcribedBy TEXT NOT NULL,
                    isComposed INTEGER NOT NULL,
                    bpm INTEGER NOT NULL,
                    bitsPerPage INTEGER NOT NULL,
                    pitchLevel INTEGER NOT NULL,
                    isEncrypted INTEGER NOT NULL,
                    updater TEXT,
                    songNotes TEXT
                )
            """.trimIndent())

                    db.execSQL("""
                INSERT INTO songs_new (id, name, author, transcribedBy, isComposed, bpm, bitsPerPage, pitchLevel, isEncrypted, updater, songNotes)
                SELECT id, name, author, transcribedBy, isComposed, bpm, bitsPerPage, pitchLevel, isEncrypted, NULL, 
                       CASE WHEN songNotes = '' THEN NULL ELSE songNotes END
                FROM songs
            """.trimIndent())

                    db.execSQL("DROP TABLE songs")
                    db.execSQL("ALTER TABLE songs_new RENAME TO songs")

                    Log.d("AppDatabase", "表结构迁移完成")

                } catch (e: Exception) {
                    Log.e("AppDatabase", "数据迁移失败", e)
                    throw e
                }
            }
        }

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                )
                    .addMigrations(MIGRATION_1_2) // 添加迁移
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.e("TAG", "onCreate: 数据库创建成功")
                            // 新数据库，标记为已迁移
                            markCompressionMigrated(context.applicationContext)
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // 只有在未进行过压缩迁移时才执行
                            if (!isCompressionMigrated(context.applicationContext)) {
                                INSTANCE?.let { database ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        convertExistingDataToCompressed(context.applicationContext, database)
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        @JvmStatic
        fun getJavaInstance(context: Context): AppDatabase {
            return getInstance(context)
        }

        /**
         * 检查是否已经进行过压缩迁移
         */
        private fun isCompressionMigrated(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_COMPRESSION_MIGRATED, false)
        }

        /**
         * 标记压缩迁移已完成
         */
        private fun markCompressionMigrated(context: Context) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit { putBoolean(KEY_COMPRESSION_MIGRATED, true) }
            Log.d("AppDatabase", "已标记压缩迁移完成")
        }

        /**
         * 转换现有数据为压缩格式
         * 这个过程是渐进式的，不会阻塞应用启动
         */
        private suspend fun convertExistingDataToCompressed(context: Context, database: AppDatabase) {
            try {
                Log.d("AppDatabase", "开始转换现有数据为压缩格式")

                val songDao = database.songDao()

                // 获取所有歌曲（不包含notes，避免一次性加载太多数据）
                val songs = songDao.getAllSongsWithoutNotesSync()

                if (songs.isEmpty()) {
                    Log.d("AppDatabase", "没有需要转换的数据")
                    markCompressionMigrated(context)
                    return
                }

                var successCount = 0
                songs.forEach { song ->
                    try {
                        // 获取包含 notes 的完整歌曲数据
                        val fullSong = songDao.getSongWithNotes(song.id)
                        fullSong?.let {
                            // 只有当 songNotes 不为空时才进行更新
                            if (!it.songNotes.isNullOrEmpty()) {
                                // 通过更新操作触发新的 TypeConverter，实现数据压缩
                                songDao.update(it)
                                successCount++
                                Log.d("AppDatabase", "已压缩歌曲: ${it.name}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AppDatabase", "转换歌曲 ${song.name} 时出错", e)
                    }
                }

                // 标记迁移完成
                markCompressionMigrated(context)
                Log.d("AppDatabase", "数据压缩转换完成，共处理 $successCount 首歌曲")

            } catch (e: Exception) {
                Log.e("AppDatabase", "批量数据转换时出错", e)
                // 即使出错也标记为已迁移，避免下次重复尝试
                markCompressionMigrated(context)
            }
        }

        /**
         * 手动重置迁移状态（调试用）
         */
        fun resetMigrationFlag(context: Context) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit { putBoolean(KEY_COMPRESSION_MIGRATED, false) }
            Log.d("AppDatabase", "已重置迁移标记")
        }
    }
}