package me.mm.auto.audio.list.database

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Song::class], version = 1)
@TypeConverters(SongNotesConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.e("TAG", "onCreate: "+"数据库创建成功" )
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        // 用于 Java 调用的实例获取方法
        @JvmStatic
        fun getJavaInstance(context: Context): AppDatabase {
            return getInstance(context)
        }
    }
}
