package me.mm.sky.auto.music.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.context.MyContext.Companion.toast
import me.mm.sky.auto.music.floatwin.FloatViewModel
import me.mm.sky.auto.music.sheet.utils.Key
import me.mm.sky.auto.music.ui.data.MainScreenViewModel
import me.mm.sky.auto.music.ui.data.music.MusicViewModel
import me.mm.sky.auto.music.ui.data.music.PlayState


class MyService : AccessibilityService() {

    companion object {
        private var _rememberState = MusicViewModel.playState
        private val serviceJob = Job()
        private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
        val viewModel = MainScreenViewModel
        fun isStart(): Boolean {
            return myService != null
        }

        var myService: MyService? = null
        suspend fun dispatchGestureClick(key: Key) = withContext(Dispatchers.IO) {
            if (myService == null) {
                MusicViewModel.pause()
                toast("无障碍服务未在运行或故障，请重新授予无障碍权限")
                return@withContext
            }
            val builder = GestureDescription.Builder()
            val path = Path()
            path.moveTo((key.x - 2).toFloat(), (key.y - 2).toFloat())
            path.lineTo((key.x + 2).toFloat(), (key.y + 2).toFloat())
            builder.addStroke(StrokeDescription(path, 0, 100))
            myService!!.dispatchGesture(builder.build(), null, null)
        }

        fun dispatchGestureClickOnNewTh(key: Key) {
            if (myService == null) {
                MusicViewModel.pause()
                toast("无障碍服务未在运行或故障，请重新授予无障碍权限")
                return
            }
            Log.e("myService", "dispatchGestureClick: click ${key}")
            val builder = GestureDescription.Builder()
            val path = Path()
            path.moveTo((key.x - 2).toFloat(), (key.y - 2).toFloat())
            path.lineTo((key.x + 2).toFloat(), (key.y + 2).toFloat())
            builder.addStroke(StrokeDescription(path, 10, 100))
            myService!!.dispatchGesture(builder.build(), null, null)
        }

        suspend fun dispatchGestureClick(keys: List<Key>) = withContext(Dispatchers.IO) {
            if (myService == null) {
                MusicViewModel.pause()
                toast("无障碍服务未在运行或故障，请重新授予无障碍权限")
                return@withContext
            }
            val builder = GestureDescription.Builder()
            for (key in keys) {
                val path = Path()
                path.moveTo((key.x - 2).toFloat(), (key.y - 2).toFloat())
                path.lineTo((key.x + 2).toFloat(), (key.y + 2).toFloat())
                builder.addStroke(StrokeDescription(path, 10, 100))
            }
            myService!!.dispatchGesture(builder.build(), null, null)
        }
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        myService = this
        MainScreenViewModel.updateIsAccGranted(true)
    }

    @SuppressLint("SwitchIntDef")
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (MainScreenViewModel.stopAll.value) {
            return
        }
        serviceScope.launch {
            event?.let {
                when (event.eventType) {
                    /*
                    AccessibilityEvent.TYPE_ANNOUNCEMENT -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_ANNOUNCEMENT")
                    }

                    AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_ASSIST_READING_CONTEXT")
                    }

                    AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_GESTURE_DETECTION_END")
                    }

                    AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_GESTURE_DETECTION_START")
                    }

                    AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {

                        Log.e("MyService", "onAccessibilityEvent: TYPE_NOTIFICATION_STATE_CHANGED")
                    }

                    AccessibilityEvent.TYPE_SPEECH_STATE_CHANGE -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_SPEECH_STATE_CHANGE")
                    }

                    AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> {
                        Log.e(
                            "MyService",
                            "onAccessibilityEvent: TYPE_TOUCH_EXPLORATION_GESTURE_END"
                        )
                    }

                    AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> {
                        Log.e(
                            "MyService",
                            "onAccessibilityEvent: TYPE_TOUCH_EXPLORATION_GESTURE_START"
                        )
                    }

                    AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_TOUCH_INTERACTION_END")
                    }

                    AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_TOUCH_INTERACTION_START")
                    }

                    AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_ACCESSIBILITY_FOCUSED")
                    }

                    AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED -> {
                        Log.e(
                            "MyService",
                            "onAccessibilityEvent: TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED"
                        )
                    }

                    AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_CLICKED")
                    }

                    AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_CONTEXT_CLICKED")
                    }

                    AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                        val rect = Rect()
                        event.source?.getBoundsInScreen(rect)
                        Log.e("MyService", "onAccessibilityEvent: ${rect}")
                    }

                    AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_HOVER_ENTER")
                    }

                    AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_HOVER_EXIT")
                    }

                    AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_LONG_CLICKED")
                    }

                    AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_SCROLLED")
                    }

                    AccessibilityEvent.TYPE_VIEW_SELECTED -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_SELECTED")
                    }

                    AccessibilityEvent.TYPE_VIEW_TARGETED_BY_SCROLL -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_TARGETED_BY_SCROLL")
                    }

                    AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_TEXT_CHANGED")
                    }

                    AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_VIEW_TEXT_SELECTION_CHANGED")
                    }

                    AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY -> {
                        Log.e(
                            "MyService",
                            "onAccessibilityEvent: TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY"
                        )
                    }

                    AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                        Log.e("MyService", "onAccessibilityEvent: TYPE_WINDOWS_CHANGED")
                    }*/
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                        handleAppChanged(event)
                        return@launch
                    }

                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                        handleAppChanged(event)
                    }

                }
            }

        }
    }

    private fun handleAppChanged(event: AccessibilityEvent?) {
        val autoHide = MainScreenViewModel.uiState.value.settingItems.find { it.key == "hide_float" }?.value as Boolean
        if (!autoHide) {
            return
        }
        val rootNode = event?.source ?: return
        val windowPackageName = rootNode.packageName
        windowPackageName.let {
            val ignoreParams = Regex("setting|system",RegexOption.IGNORE_CASE)
            val skyParams=Regex("sky",RegexOption.IGNORE_CASE)
            if (windowPackageName.contains(ignoreParams)){
                return
            }else if (windowPackageName.contains(skyParams)){
                FloatViewModel.autoUnHideFloat()
            } else {
                FloatViewModel.autoHideFloat()
                MusicViewModel.pause()
            }
        }
    }

    private fun handleWindowsChanged() {
        val windows = rootInActiveWindow
        Log.e("MyService", "windows: ${windows.childCount}")
        var noOverlay = MutableStateFlow(true)
        _rememberState = MusicViewModel.playState
        for (i in 0 until windows.childCount) {
            if (windows == null) {
                return
            }
            if (windows.childCount <= i) {
                return
            }
            val window = windows.getChild(i)
            val rect = Rect()
            window.getBoundsInScreen(rect)
            Log.e("MyService", "RECT: $rect")
            if (FloatViewModel.isOverKeyboard(rect)) {
                noOverlay.value = false
                return
            }

        }
        if (!noOverlay.value) {
            MusicViewModel.pause()
        } else if (_rememberState.value == PlayState.PLAYING) {
            MusicViewModel.play()
        }
    }


    override fun onInterrupt() {
        toast("辅助服务被迫中断")

    }

    override fun onUnbind(intent: Intent?): Boolean {
        toast("辅助服务已关闭")
        myService = null
        serviceJob.cancel()
        viewModel.updateIsAccGranted(false)
        return super.onUnbind(intent)
    }
}