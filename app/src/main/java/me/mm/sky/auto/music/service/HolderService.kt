package me.mm.sky.auto.music.service

import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import me.mm.sky.auto.music.ui.data.MainScreenViewModel


class HolderService : Service() {
    companion object {
        private val handler = Handler(Looper.getMainLooper())
        var holderService: HolderService? = null
        fun toast(msg: String) {
            if (holderService == null) {
                return
            }
            handler.post {
                Toast.makeText(holderService, msg, Toast.LENGTH_SHORT).show()
            }
        }

        fun editBoolean(key: String, value: Boolean) {
            if (holderService == null) {
                return
            }
            holderService?.getSharedPreferences("data", MODE_PRIVATE)?.edit()
                ?.putBoolean(key, value)?.apply()
        }

        fun getBoolean(key: String, defValue: Boolean): Boolean {
            if (holderService == null) {
                return defValue
            }
            return holderService?.getSharedPreferences("data", MODE_PRIVATE)
                ?.getBoolean(key, defValue) ?: defValue
        }

        fun editString(key: String, value: String) {
            if (holderService == null) {
                return
            }
            holderService?.getSharedPreferences("data", MODE_PRIVATE)?.edit()?.putString(key, value)
                ?.apply()
        }

        fun getString(key: String, defValue: String): String {
            if (holderService == null) {
                return defValue
            }
            return holderService?.getSharedPreferences("data", MODE_PRIVATE)
                ?.getString(key, defValue) ?: defValue
        }

        fun isStart(): Boolean {
            return holderService != null
        }

        fun getInt(key: String, defValue: Int): Int {
            if (holderService == null) {
                return defValue
            }
            return holderService?.getSharedPreferences("data", MODE_PRIVATE)?.getInt(key, defValue)
                ?: defValue
        }

        fun editInt(key: String, value: Int) {
            if (holderService == null) {
                return
            }
            holderService?.getSharedPreferences("data", MODE_PRIVATE)?.edit()?.putInt(key, value)
                ?.apply()
        }

        fun getIsNotificationGranted(): Boolean {
            if (holderService == null) {
                return false
            }
            return NotificationManagerCompat.from(holderService!!).areNotificationsEnabled()
        }

        fun getIsFloatWindowGranted(context: Context? = holderService): Boolean {
            if (context == null) {
                return false
            }
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

        fun hideTask(exclude: Boolean) {
            if (holderService == null) {
                return
            }
            val activityManager =
                holderService!!.applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val taskInfo = activityManager.appTasks
            for (task in taskInfo) {
                task.setExcludeFromRecents(exclude)
            }
        }
    }


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        holderService = this
        toast("服务已启动")
        hideTask(true)
//        MainScreenViewModel.updateIsFloatWindowGranted(HolderService.getIsFloatWindowGranted())
        return super.onStartCommand(intent, flags, startId)

    }
}