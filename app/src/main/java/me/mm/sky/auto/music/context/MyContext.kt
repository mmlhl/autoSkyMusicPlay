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
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import me.mm.auto.audio.list.database.AppDatabase
import me.mm.sky.auto.music.floatwin.FloatingWindowService
import me.mm.sky.auto.music.service.HolderService
import me.mm.sky.auto.music.service.MyService
import me.mm.sky.auto.music.tools.AccessibilityUtils
import me.mm.sky.auto.music.ui.data.MainScreenViewModel
import me.mm.sky.auto.music.ui.setting.SettingItem


class MyContext : Application() {
    companion object {
        lateinit var database: AppDatabase
        lateinit var floatingWindowService: FloatingWindowService
        lateinit var context: MyContext
        private val handler = Handler(Looper.getMainLooper())
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
            val uiState = MainScreenViewModel.uiState.value
            uiState.settingItems.forEach {
                when (it.key) {
                    "hide_task" -> {
                        hideTask(it.value as Boolean)
                    }
                }
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        context = this
        database = AppDatabase.getInstance(context)
        if (!FloatingWindowService.isServiceRunning()) {
            context.startService(Intent(context, FloatingWindowService::class.java))
        }
        val uiState = MainScreenViewModel.uiState.value

        // 读取配置
        uiState.settingItems.forEach {
            when (it.key) {
                "root_auto_acc" -> {
                    if (it.value as Boolean) {
                        if (!isAccessibilityEnabled()) {
                            AccessibilityUtils.enableAccessibilityService(MyService::class.java.name)
                        }
                    }
                }
                "hide_task" -> {
                    if (it.value as Boolean) {
                        hideTask(true)
                    }
                }
            }
        }
        // 根据开启的权限情况，更新HomeScreen内容
        MainScreenViewModel.uiState.value = uiState.copy(
            isFloatWindowGranted = getIsFloatWindowGranted(),
            isNotificationGranted = getIsNotificationGranted(),
            isAccGranted = isAccessibilityEnabled()
        )
    }
}