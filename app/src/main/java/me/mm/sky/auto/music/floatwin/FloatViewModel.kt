package me.mm.sky.auto.music.floatwin

import android.annotation.SuppressLint
import android.view.ContextThemeWrapper
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petterp.floatingx.FloatingX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.tools.GlobalAlertDialog
import me.mm.sky.auto.music.ui.data.music.MusicViewModel

@SuppressLint("StaticFieldLeak")
object FloatViewModel : ViewModel() {
    private val _floatState = MutableStateFlow(FloatState())
    private val _getLocationShowing = MutableStateFlow(false)
    private val nowAutoHide=MutableStateFlow(false)
    val getLocationShowing = _getLocationShowing
    private val _noRemainMeAgain = MutableStateFlow(MyContext.getBoolean("noRemainMeAgain", false))
    val floatState = _floatState


    fun updateLocationShowing(isShowing: Boolean) {
        viewModelScope.launch {
            _getLocationShowing.value = isShowing
            if (isShowing) {
                MusicViewModel.pause()
                FloatingX.control("getLocation").show()
            } else {
                FloatingX.control("getLocation").hide()
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun upDateNoRemain(noRemain: Boolean) {
        _noRemainMeAgain.value = noRemain
        viewModelScope.launch {
            MyContext.editBoolean("noRemainMeAgain", noRemain)
        }
    }

    fun updateFloatState(floatSateEnum: FloatSateEnum) {
        _floatState.value = _floatState.value.copy(
            floatSateEnum = floatSateEnum
        )
        viewModelScope.launch {
            when (floatSateEnum) {
                FloatSateEnum.FLOAT_LIST -> {
                    val smallIcon = FloatingX.control("smallIcon")
                    val floating = FloatingX.control("floating")
                    floating.show()
                    smallIcon.hide()
                }

                FloatSateEnum.FLOAT_SMALL_ICON -> {
                    if (!_noRemainMeAgain.value) {
                        val themedContext = ContextThemeWrapper(
                            MyContext.context,
                            androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert
                        )
                        val builder: AlertDialog.Builder = AlertDialog.Builder(themedContext)
                        builder.setTitle("提示")
                        builder.setMessage("长按悬浮球恢复显示歌曲列表")
                        builder.setNegativeButton("确定") { dialog, which ->
                            dialog.dismiss()
                        }
                        builder.setNeutralButton("知道了") { dialog, which ->
                            dialog.dismiss()
                        }
                        builder.setPositiveButton("不再提示") { dialog, which ->
                            upDateNoRemain(true)
                            dialog.dismiss()
                        }
                        GlobalAlertDialog.showGlobalAlertDialog(builder)
                    }

                    FloatingX.control("floating").hide()
                    FloatingX.control("smallIcon").show()
                }

                FloatSateEnum.FLOAT_NONE -> {
                    FloatingX.control("floating").hide()
                    FloatingX.control("smallIcon").hide()
                }
            }

        }
    }
}