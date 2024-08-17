package me.mm.sky.auto.music.ui.data

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.petterp.floatingx.FloatingX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.context.MyContext.Companion.editBoolean
import me.mm.sky.auto.music.context.MyContext.Companion.editInt
import me.mm.sky.auto.music.context.MyContext.Companion.editString
import me.mm.sky.auto.music.tools.AccessibilityUtils
import me.mm.sky.auto.music.ui.HomeScreen
import me.mm.sky.auto.music.ui.data.music.MusicViewModel
import me.mm.sky.auto.music.ui.setting.SettingItem
import me.mm.sky.auto.music.ui.setting.SettingType

object MainScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    var uiState = _uiState

    fun updateSettingItem(item: SettingItem, value: Any) {
        _uiState.value = _uiState.value.copy(
            settingItems = _uiState.value.settingItems.map {
                when (it.key) {
                    "hide_task" -> {
                        MyContext.hideTask(value as Boolean)
                    }

                }
                if (it.key == item.key) {
                    saveSetting(item, value)
                    it.copy(value = value)
                } else {
                    it
                }
            }
        )
    }
    fun files2Db() {
        viewModelScope.launch {
            MusicViewModel.loadSongs()
            MyContext.files2Db()
        }

    }
    fun stopAllService() {
        MusicViewModel.stop()
        FloatingX.control("floating").hide()
        FloatingX.control("smallIcon").hide()
    }
    fun updateStartStatue(isStart: Boolean) {
        _uiState.value = _uiState.value.copy(startStatue = isStart)


    }
    fun rootStartService() {
        viewModelScope.launch {
            AccessibilityUtils.enableAccessibilityService("me.mm.sky.auto.music/.service.MyService")
        }

    }

    fun updateIsAccGranted(isGranted: Boolean) {
        _uiState.value = _uiState.value.copy(isAccGranted = isGranted)
    }

    fun updateIsFloatWindowGranted(isGranted: Boolean) {
        _uiState.value = _uiState.value.copy(isFloatWindowGranted = isGranted)
    }

    fun updateHideTask(isHide: Boolean) {
        MyContext.hideTask(isHide)
    }

    fun updateCurrentScreen(homeScreen: HomeScreen) {
        //如果当前的屏幕是homeScreen，那么就不需要更新
        if (_uiState.value.currentScreen == homeScreen) {
            return
        }
        _uiState.value = _uiState.value.copy(currentScreen = homeScreen)

    }

    fun updateIsNotificationGranted(isGranted: Boolean) {
        _uiState.value = _uiState.value.copy(isNotificationGranted = isGranted)
    }

    private fun saveSetting(item: SettingItem, value: Any) {
        when (item.type) {
            SettingType.BOOLEAN -> {
                editBoolean(item.key, value as Boolean)
            }

            SettingType.STRING -> {
                editString(item.key, value as String)
            }

            SettingType.INT -> {
                editInt(item.key, value as Int)
            }

            SettingType.SELECT -> {
                editString(item.key, value as String)
            }
        }
    }
}