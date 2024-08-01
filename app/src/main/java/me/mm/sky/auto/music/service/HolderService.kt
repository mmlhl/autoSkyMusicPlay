package me.mm.sky.auto.music.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Job


class HolderService : Service() {
    companion object {
        private val handler = Handler(Looper.getMainLooper())
        var holderService: HolderService? = null

        var job: Job? = null
        fun isStart(): Boolean {
            return holderService != null
        }


    }


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        holderService = this
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}