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
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.context.MyContext.Companion.toast
import me.mm.sky.auto.music.ui.data.MainScreenViewModel


class HolderService : Service() {
    companion object {
        private val handler = Handler(Looper.getMainLooper())
        var holderService: HolderService? = null


        fun isStart(): Boolean {
            return holderService != null
        }


    }


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        holderService = this
//        hideTask(true)
        return super.onStartCommand(intent, flags, startId)

    }
}