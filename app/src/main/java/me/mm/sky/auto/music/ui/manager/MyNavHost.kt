package me.mm.sky.auto.music.ui.manager

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import me.mm.sky.auto.music.ui.HomeScreen
import me.mm.sky.auto.music.ui.musicpage.MusicScreenPage
import me.mm.sky.auto.music.ui.homepage.HomeScreenPage
import me.mm.sky.auto.music.ui.setting.SettingScreenPage

@Composable
fun MyNavHost(
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    androidx.navigation.compose.NavHost(
        navController = navHostController,
        startDestination = HomeScreen.HOME.route,
        modifier = modifier
    ) {
        HomeScreen.entries.forEach { screen ->
            composable(screen.route) {
                // 传入 navController 给页面
                screen.context(navHostController)()
            }
        }
    }
}
