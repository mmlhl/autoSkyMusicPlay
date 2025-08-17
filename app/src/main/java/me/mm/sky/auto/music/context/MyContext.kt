package me.mm.sky.auto.music.context

import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.mm.sky.auto.music.database.AppDatabase
import me.mm.sky.auto.music.database.Song
import me.mm.sky.auto.music.floatwin.FloatingWindowService
import me.mm.sky.auto.music.sheet.utils.Key
import me.mm.sky.auto.music.tools.FileUtils
import me.mm.sky.auto.music.ui.data.MainActivityViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class MyContext : Application() {
    companion object {
        lateinit var database: AppDatabase
        lateinit var floatingWindowService: FloatingWindowService
        lateinit var context: MyContext
        private val handler = Handler(Looper.getMainLooper())
        fun escapeUnescapedBackslashes(input: String): String {
            val regex = Regex("""(?<!\\)\\(?![\\/"bfnrtu])""")
            return regex.replace(input) { "\\\\" }
        }

        fun removeControlChars(input: String): String {
            val sb = StringBuilder()
            for (ch in input) {
                // 过滤掉所有控制字符（0x00 - 0x1F）
                if (ch.code >= 0x20) {
                    sb.append(ch)
                }
            }
            return sb.toString()
        }

        fun cleanJsonString(input: String): String {
            val step1 = escapeUnescapedBackslashes(input)
            val step2 = removeControlChars(step1)
            return step2
        }


        fun toast(msg: String) {
            //转到主线程，弹窗
            handler.post {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }

        fun editBoolean(key: String, value: Boolean) {
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putBoolean(key, value).apply()
        }

        fun getBoolean(key: String, defValue: Boolean): Boolean {
            return context.getSharedPreferences("data", MODE_PRIVATE).getBoolean(key, defValue)
        }

        fun editString(key: String, value: String) {
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putString(key, value).apply()
        }

        fun getString(key: String, defValue: String): String {
            return context.getSharedPreferences("data", MODE_PRIVATE).getString(key, defValue)
                ?: defValue
        }

        fun editInt(key: String, value: Int) {
            context.getSharedPreferences("data", MODE_PRIVATE).edit().putInt(key, value).apply()
        }

        fun getInt(key: String, defValue: Int): Int {
            return context.getSharedPreferences("data", MODE_PRIVATE).getInt(key, defValue)
        }

        fun getIsNotificationGranted(): Boolean {
            return NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        fun getIsFloatWindowGranted(): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appOpsMgr =
                    context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode = appOpsMgr.checkOpNoThrow(
                    "android:system_alert_window", android.os.Process.myUid(), context
                        .packageName
                )
                mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED
            } else {
                Settings.canDrawOverlays(context)
            }

        }

        fun isAccessibilityEnabled(): Boolean {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return enabledServices?.contains(context.packageName) == true
        }
        fun hideTask(exclude: Boolean) {
            val activityManager =
                context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val taskInfo = activityManager.appTasks
            for (task in taskInfo) {
                task.setExcludeFromRecents(exclude)
            }
        }
        fun updateHideTask() {
            val uiState = MainActivityViewModel.uiState.value
            uiState.settingItems.forEach {
                when (it.key) {
                    "hide_task" -> {
                        hideTask(it.value as Boolean)
                    }
                }
            }
        }
        suspend fun files2Db( ) {
            withContext(Dispatchers.IO) {
                val songList = mutableListOf<Song>()
                val songDao = database.songDao()
                val filePath = context.getExternalFilesDirs(null).get(0)
                val directory = File(filePath, "sheets")
                if (directory.exists() && directory.isDirectory) {
                    val files = directory.listFiles() ?: return@withContext
                    for (file in files) {
                        Log.e("MyContext", "files2Db: "+file.name )
                        if (file.isFile && file.extension == "txt") {
                            try {
                                val strings = FileUtils.readTextFile(file.absolutePath)
                                val jsonString=cleanJsonString(strings)
//                                Log.e("MyContext", "files2Db: "+jsonString )
                                val jsonArray = JsonParser.parseString(jsonString).asJsonArray
                                val firstElement = jsonArray[0]
                                val song: Song = Gson().fromJson(firstElement, Song::class.java)
                                if (songDao.existSong(file.nameWithoutExtension) == 0) {
                                    song.name = file.nameWithoutExtension
                                    songList.add(song)
                                }
                                file.delete()
                            } catch (e: IOException) {
                                // 处理读取文件时的 IO 异常
                                Log.e("files2Db", "读取文件 ${file.name} 时出错: ${e.message}")
                            } catch (e: JsonSyntaxException) {
                                // 处理 JSON 解析异常
                                Log.e("files2Db", "解析文件 ${file.name} 时出错: ${e.message}")
                            }
                        }
                    }
                }
                for (song in songList) {
                    songDao.insert(song)
                }
            }
        }


    }

    override fun onCreate() {
        super.onCreate()
        context = this
        database = AppDatabase.getInstance(context)
        if (getBoolean("firstStart",true)){
            copyAssetsToPrivateStorage("sheets")
        }
        initKeyMap()
        MainActivityViewModel.files2Db()
        if (!FloatingWindowService.isServiceRunning()) {
            context.startService(Intent(context, FloatingWindowService::class.java))
        }

    }
    private fun initKeyMap() {
        val x0=getInt("x0",0)
        val y0=getInt("y0",0)
        val x1=getInt("x1",0)
        val y1=getInt("y1",0)
        if (x0==0||y0==0||x1==0||y1==0){
            return
        }
        Key.init(x0,y0,x1,y1)
    }
    private fun copyAssetsToPrivateStorage(assetsSubdirectory: String) {
        try {
            val assetFiles = assets.list(assetsSubdirectory) ?: emptyArray()
            for (assetFile in assetFiles) {
                val inputStream = assets.open("$assetsSubdirectory/$assetFile")
                val outputDir = File(getExternalFilesDir(null), assetsSubdirectory)
                if (!outputDir.exists()) {
                    outputDir.mkdirs()
                }
                val outputFile = File(outputDir, assetFile)
                val outputStream = FileOutputStream(outputFile)

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            }
            editBoolean("firstStart",false)
        } catch (e: IOException) {
            Log.e("MainActivity", "复制 assets 文件时出错: ${e.message}")
        }
    }

    fun copyTxtFilesToSheets(context: Context) {
        val externalFilesDir = context.getExternalFilesDirs(null).firstOrNull() ?: return
        val sheetsDir = File(externalFilesDir, "sheets")
        if (!sheetsDir.exists()) {
            sheetsDir.mkdirs()
        }
        val txtFiles = externalFilesDir.listFiles { file -> file.isFile && file.extension == "txt" }
        txtFiles?.forEach { txtFile ->
            val destinationFile = File(sheetsDir, txtFile.name)
            copyFile(txtFile, destinationFile)
        }
    }

    private fun copyFile(sourceFile: File, destinationFile: File) {
        FileInputStream(sourceFile).use { input ->
            FileOutputStream(destinationFile).use { output ->
                val buffer = ByteArray(1024)
                var length: Int
                while (input.read(buffer).also { length = it } > 0) {
                    output.write(buffer, 0, length)
                }
            }
        }
    }

}