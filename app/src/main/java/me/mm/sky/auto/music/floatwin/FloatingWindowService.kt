package me.mm.sky.auto.music.floatwin

import FloatingWindowContent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.petterp.floatingx.FloatingX
import com.petterp.floatingx.assist.FxScopeType
import com.petterp.floatingx.compose.FxComposeLifecycleOwner
import com.petterp.floatingx.compose.enableComposeSupport
import com.petterp.floatingx.listener.IFxTouchListener
import com.petterp.floatingx.listener.control.IFxAppControl
import com.petterp.floatingx.view.IFxInternalHelper
import me.mm.sky.auto.music.MainActivity
import me.mm.sky.auto.music.R
import me.mm.sky.auto.music.context.MyContext


class FloatingWindowService : Service() {

    companion object {
        val lifecycleOwner = FloatingWindowLifecycleOwner()
        lateinit var composeView: ComposeView
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        )

        val windowManager =
            MyContext.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var floatingWindowService: FloatingWindowService? = null
        fun isServiceRunning(): Boolean {
            return floatingWindowService != null
        }

        lateinit var floatContent: IFxAppControl
        lateinit var floatGetLocation: IFxAppControl
        lateinit var floatSmallIcon: IFxAppControl
        fun updateFloatGetLocation() {
            if (floatGetLocation.isShow()) {
                floatGetLocation.hide()
            } else {
                floatGetLocation.show()
            }
        }
    }

    init {
        composeView = ComposeView(MyContext.context).apply {
            setContent {
                GetKeyLocationWindow()
            }
        }
        val owner= FxComposeLifecycleOwner()
        composeView.setViewTreeLifecycleOwner(owner)
        composeView.setViewTreeViewModelStoreOwner(owner)
        composeView.setViewTreeSavedStateRegistryOwner(owner)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        floatContent = FloatingX.install {
            setTag("floating")
            setContext(MyContext.context)
            setScopeType(FxScopeType.SYSTEM)
            setEnableAnimation(true)
            setLayoutView(ComposeView(MyContext.context).apply {
                setContent {
                    FloatingWindowContent() {
                        FloatingX.control(tag = "floating").hide()
                    }
                }
            })
            enableComposeSupport()
            setTouchListener(object : IFxTouchListener {
                override fun onInterceptTouchEvent(
                    event: MotionEvent,
                    control: IFxInternalHelper?
                ): Boolean {
                    val isAdView = control?.checkPointerDownTouch(R.id.frameLayout, event)
                    return isAdView ?: true
                }
            })
        }

        floatGetLocation = FloatingX.install {
             /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val displayMetrics =windowManager.currentWindowMetrics
                val insets = displayMetrics.windowInsets

                val cutout = insets.displayCutout
                if (cutout != null) {
                    val safeInsets = cutout.boundingRects
                    safeInsets.forEach {
                        Log.e("TAG", "onCreate: safeInsets:\n X:${it.left}	Y:${it.top}	X1:${it.right}	Y2:${it.bottom}")
                    }
                }
            } else {
                TODO("VERSION.SDK_INT < R")
            }*/
            setTag("getLocation")
            setContext(MyContext.context)
            setScopeType(FxScopeType.SYSTEM)
            setEnableScrollOutsideScreen(true)
            setLeftBorderMargin(-10f)
            setEnableAnimation(true)
            setManagerParams(
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                )
            )
            enableComposeSupport()
            setLayoutView(ComposeView(MyContext.context).apply {
                setContent {
                    GetKeyLocationWindow()
                }
            })
        }

        floatSmallIcon = FloatingX.install {
            setTag("smallIcon")
            setContext(MyContext.context)
            setScopeType(FxScopeType.SYSTEM)
            setEnableAnimation(true)

            setManagerParams(
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                )
            )
            enableComposeSupport()
            setLayoutView(ComposeView(MyContext.context).apply {
                setContent {
                    SmallIconFloat()
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }

}
