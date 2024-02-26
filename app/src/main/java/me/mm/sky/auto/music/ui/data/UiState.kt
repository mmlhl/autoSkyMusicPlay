package me.mm.sky.auto.music.ui.data

import android.app.Notification
import me.mm.sky.auto.music.service.HolderService
import me.mm.sky.auto.music.ui.HomeScreen

data class UiState(
    val settingItems: List<SettingItem> = listOf(
    ),
    val isAccGranted: Boolean = false,
    val isFloatWindowGranted: Boolean = false,
    val isNotificationGranted: Boolean = HolderService.getIsNotificationGranted(),
    val screens: List<HomeScreen> = HomeScreen.entries,
    val currentScreen: HomeScreen = HomeScreen.HOME
) {

}
