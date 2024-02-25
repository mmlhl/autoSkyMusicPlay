package me.mm.sky.auto.music.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

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
            holderService?.getSharedPreferences("data", MODE_PRIVATE)?.edit()?.putBoolean(key, value)?.apply()
        }
        fun getBoolean(key: String, defValue: Boolean): Boolean {
            if (holderService == null) {
                return defValue
            }
            return holderService?.getSharedPreferences("data", MODE_PRIVATE)?.getBoolean(key, defValue) ?: defValue
        }
        fun editString(key: String, value: String) {
            if (holderService == null) {
                return
            }
            holderService?.getSharedPreferences("data", MODE_PRIVATE)?.edit()?.putString(key, value)?.apply()
        }
        fun getString(key: String, defValue: String): String {
            if (holderService == null) {
                return defValue
            }
            return holderService?.getSharedPreferences("data", MODE_PRIVATE)?.getString(key, defValue) ?: defValue
        }
        fun isStart(): Boolean {
            return holderService != null
        }
        fun getInt(key: String, defValue: Int): Int {
            if (holderService == null) {
                return defValue
            }
            return holderService?.getSharedPreferences("data", MODE_PRIVATE)?.getInt(key, defValue) ?: defValue
        }
        fun editInt(key: String, value: Int) {
            if (holderService == null) {
                return
            }
            holderService?.getSharedPreferences("data", MODE_PRIVATE)?.edit()?.putInt(key, value)?.apply()
        }
        fun getIsNotificationGranted(): Boolean {
            if (holderService == null) {
                return false
            }
            return NotificationManagerCompat.from(holderService!!).areNotificationsEnabled()
        }
    }




    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        holderService = this
        toast("服务已启动")
        return super.onStartCommand(intent, flags, startId)
    }
}