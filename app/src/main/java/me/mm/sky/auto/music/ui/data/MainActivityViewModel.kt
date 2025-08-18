package me.mm.sky.auto.music.ui.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.petterp.floatingx.FloatingX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.floatwin.FloatSateEnum
import me.mm.sky.auto.music.floatwin.FloatViewModel
import me.mm.sky.auto.music.tools.PermissionUtils
import me.mm.sky.auto.music.ui.HomeScreen
import me.mm.sky.auto.music.ui.data.music.MusicViewModel

class MainActivityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainState())
    var uiState = _uiState
    private val _stopAll = MutableStateFlow(false)
    var stopAll = _stopAll


    fun files2Db() {
        viewModelScope.launch {
            MusicViewModel.loadSongs()
            MyContext.files2Db()
        }

    }

    fun stopAllService() {
        MusicViewModel.stop()
        FloatViewModel.updateFloatState(FloatSateEnum.FLOAT_NONE)
        FloatingX.control("floating").hide()
        FloatingX.control("smallIcon").hide()
    }

    fun updateStartStatue(isStart: Boolean) {
        _uiState.value = _uiState.value.copy(startStatue = isStart)
    }

    fun rootStartService() {
        viewModelScope.launch {
            PermissionUtils.enableAccessibilityService()
        }

    }



    fun updateCurrentScreen(homeScreen: HomeScreen) {
        //如果当前的屏幕是homeScreen，那么就不需要更新
        if (_uiState.value.currentScreen == homeScreen) {
            return
        }
        _uiState.value = _uiState.value.copy(currentScreen = homeScreen)

    }


}