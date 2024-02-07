package me.mm.sky.auto.music.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast

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
    }




    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        holderService = this
        toast("HolderService启动")
        return super.onStartCommand(intent, flags, startId)
    }
}