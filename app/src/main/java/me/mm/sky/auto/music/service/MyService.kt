package me.mm.sky.auto.music.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.os.Build
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import me.mm.sky.auto.music.MainActivity
import me.mm.sky.auto.music.context.MyContext.Companion.toast
import me.mm.sky.auto.music.ui.data.MainScreenViewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MyService : AccessibilityService() {

    companion object {
        val viewModel = MainScreenViewModel
        fun isStart(): Boolean {
            return myService != null
        }

        var myService: MyService? = null

    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        myService = this
        toast("辅助服务已开启")
        viewModel.updateIsAccGranted(true)
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }


    override fun onInterrupt() {


    }

    override fun onUnbind(intent: Intent?): Boolean {
        toast("辅助服务已关闭")
        myService = null
        viewModel.updateIsAccGranted(false)
        return super.onUnbind(intent)
    }
}