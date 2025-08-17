package me.mm.sky.auto.music.ui.data

import me.mm.sky.auto.music.R
import me.mm.sky.auto.music.context.MyContext.Companion.getBoolean
import me.mm.sky.auto.music.context.MyContext.Companion.getIsNotificationGranted
import me.mm.sky.auto.music.ui.HomeScreen
import me.mm.sky.auto.music.ui.setting.SettingItem
import me.mm.sky.auto.music.ui.setting.SettingType

data class MainState(
    val settingItems: List<SettingItem> = listOf(
        SettingItem("hide_float", SettingType.BOOLEAN, getBoolean("hide_float",false), R.string.hide_float_title,R.string.hide_float_des ),
        SettingItem("root_auto_acc", SettingType.BOOLEAN, getBoolean("root_auto_acc",false), R.string.root_auto_acc_title,R.string.root_auto_acc_des ),
        SettingItem("hide_task", SettingType.BOOLEAN, getBoolean("hide_task",false), R.string.hide_task_title,R.string.hide_task_des ),

    ),
    var startStatue: Boolean = false,
    val screens: List<HomeScreen> = HomeScreen.entries,
    val currentScreen: HomeScreen = HomeScreen.HOME
) {
}
