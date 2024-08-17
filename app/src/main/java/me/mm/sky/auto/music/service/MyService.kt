package me.mm.sky.auto.music.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.accessibilityservice.GestureDescription.StrokeDescription
import android.content.Intent
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.context.MyContext.Companion.toast
import me.mm.sky.auto.music.floatwin.FloatSateEnum
import me.mm.sky.auto.music.floatwin.FloatViewModel
import me.mm.sky.auto.music.sheet.utils.Key
import me.mm.sky.auto.music.ui.data.MainScreenViewModel
import me.mm.sky.auto.music.ui.data.music.MusicViewModel


class MyService : AccessibilityService() {

    companion object {
        val viewModel = MainScreenViewModel
        fun isStart(): Boolean {
            return myService != null
        }
        var myService: MyService? = null
        suspend fun dispatchGestureClick(key: Key) = withContext(Dispatchers.IO) {
            if (myService == null) {
                MusicViewModel.pause()
                Toast.makeText(MyContext.context, "无障碍服务未在运行或故障，请重新授予无障碍权限", Toast.LENGTH_SHORT).show()
                return@withContext
            }
            val builder = GestureDescription.Builder()
            val path = Path()
            path.moveTo((key.x - 2).toFloat(), (key.y - 2).toFloat())
            path.lineTo((key.x + 2).toFloat(), (key.y + 2).toFloat())
            builder.addStroke(StrokeDescription(path, 0, 50))
            myService!!.dispatchGesture(builder.build(), null, null)
        }
        fun dispatchGestureClickOnNewTh(key: Key){
            if (myService == null) {
                MusicViewModel.pause()
                Toast.makeText(MyContext.context, "无障碍服务未在运行或故障，请重新授予无障碍权限", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(MyContext.context, "无障碍服务未在运行或故障，请重新授予无障碍权限", Toast.LENGTH_SHORT).show()
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

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        event?.let {
            when (event.eventType) {
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    val rootNode = event.source
                    if (rootNode != null) {
                        val windowPackageName = rootNode.packageName
                        windowPackageName.let {
                            if (windowPackageName.contains(
                                    "setting"
                                ))return
                            if (windowPackageName.contains(
                                    "sky"
                            )) {
                                FloatViewModel.autoUnHideFloat()
                            }
                            else{
                                FloatViewModel.autoHideFloat()
                            }
                        }
                    }
                }

                AccessibilityEvent.TYPE_ANNOUNCEMENT -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_SPEECH_STATE_CHANGE -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_SELECTED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_TARGETED_BY_SCROLL -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                    TODO()
                }

                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                    TODO()
                }
            }
        }

    }





    override fun onInterrupt() {
        toast("辅助服务被迫中断")

    }

    override fun onUnbind(intent: Intent?): Boolean {
        toast("辅助服务已关闭")
        myService = null
        viewModel.updateIsAccGranted(false)
        return super.onUnbind(intent)
    }
}