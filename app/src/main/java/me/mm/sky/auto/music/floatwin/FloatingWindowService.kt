package me.mm.sky.auto.music.floatwin

import FloatingWindowContent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.EditText
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import com.petterp.floatingx.FloatingX
import com.petterp.floatingx.assist.FxDisplayMode
import com.petterp.floatingx.assist.FxScopeType
import com.petterp.floatingx.compose.enableComposeSupport
import com.petterp.floatingx.listener.control.IFxAppControl
import me.mm.sky.auto.music.context.MyContext


class FloatingWindowService : Service() {

    companion object{
        val floatViewModel = FloatViewModel
        var floatingWindowService: FloatingWindowService?=null
        fun isServiceRunning(): Boolean {
            return floatingWindowService != null
        }
        lateinit var floatContent:IFxAppControl
        fun updateFloatState(floatSateEnum: FloatSateEnum) {
            when (floatSateEnum) {
                FloatSateEnum.FLOAT_SMALL_ICON -> {
                    floatViewModel.updateFloatState(FloatSateEnum.FLOAT_SMALL_ICON)
                    floatContent.show()
                }

                FloatSateEnum.FLOAT_LIST -> {

                    floatViewModel.updateFloatState(FloatSateEnum.FLOAT_LIST)
                }
                FloatSateEnum.FLOAT_NONE -> {
                    floatViewModel.updateFloatState(FloatSateEnum.FLOAT_NONE)
                    floatContent.hide()
                }
            }
        }

    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val editText= EditText(MyContext.context)
        editText.setText("123")
        floatContent=FloatingX.install {
            setTag("floating")

            setContext(MyContext.context)
//            setScopeType(FxScopeType.SYSTEM)

            setEnableAnimation(true)
            setLayoutView(ComposeView(MyContext.context).apply {
                setContent {
                    FloatingWindowContent(floatViewModel) {
                        FloatingX.control(tag = "floating").hide()
                    }
                }
            })
            enableComposeSupport()

        }

    }

    override fun onDestroy() {
        super.onDestroy()

    }

}
