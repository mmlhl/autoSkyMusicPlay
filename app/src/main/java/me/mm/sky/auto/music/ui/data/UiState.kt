package me.mm.sky.auto.music.ui.data

import me.mm.sky.auto.music.R
import me.mm.sky.auto.music.context.MyContext.Companion.getBoolean
import me.mm.sky.auto.music.context.MyContext.Companion.getIsNotificationGranted
import me.mm.sky.auto.music.ui.HomeScreen
import me.mm.sky.auto.music.ui.setting.SettingItem
import me.mm.sky.auto.music.ui.setting.SettingType

data class UiState(
    val homeSettingItems: List<SettingItem> = listOf(
    ),
    val settingItems: List<SettingItem> = listOf(
        SettingItem("root_auto_acc", SettingType.BOOLEAN, getBoolean("root_auto_acc",false), R.string.root_auto_acc_title,R.string.root_auto_acc_des ),
        SettingItem("hide_task", SettingType.BOOLEAN, getBoolean("hide_task",false), R.string.hide_task_title,R.string.hide_task_des ),
    ),
    val isAccGranted: Boolean = false,
    val isFloatWindowGranted: Boolean = false,
    val isNotificationGranted: Boolean = getIsNotificationGranted(),
    val screens: List<HomeScreen> = HomeScreen.entries,
    val currentScreen: HomeScreen = HomeScreen.HOME
) {
}
