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
fun MyNavHost(navHostController: NavHostController,modifier: Modifier) {
    NavHost(navController = navHostController,modifier=modifier, startDestination = HomeScreen.HOME.route){
        composable(HomeScreen.HOME.route){
            HomeScreenPage(modifier = modifier)
        }
        composable(HomeScreen.MUSIC.route){
            MusicScreenPage(modifier = modifier)
        }
        composable(HomeScreen.SETTINGS.route){
            SettingScreenPage(modifier = modifier)
        }
    }
}