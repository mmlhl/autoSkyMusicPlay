package me.mm.sky.auto.music.service

import android.accessibilityservice.AccessibilityService
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.accessibility.AccessibilityEvent
import me.mm.sky.auto.music.service.HolderService.Companion.toast

class MyService : AccessibilityService() {

    companion object {
        fun isStart(): Boolean {
            return myService != null
        }
        var myService: MyService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        myService = this
        toast("辅助服务已开启")
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }

    override fun onInterrupt() {
        myService = null
        toast("辅助服务已关闭")
    }
}