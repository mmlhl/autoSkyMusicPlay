package me.mm.sky.auto.music.ui.data

import me.mm.sky.auto.music.ui.HomeScreen

data class MainState(

    var startStatue: Boolean = false,
    val screens: List<HomeScreen> = HomeScreen.entries,
    val currentScreen: HomeScreen = HomeScreen.HOME
)
