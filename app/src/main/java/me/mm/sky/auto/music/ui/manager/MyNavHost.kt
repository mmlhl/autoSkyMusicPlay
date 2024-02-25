package me.mm.sky.auto.music.ui.manager

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import me.mm.sky.auto.music.ui.HomeScreen
import me.mm.sky.auto.music.ui.homepage.HomeScreenPage

@Composable
fun MyNavHost(navHostController: NavHostController,modifier: Modifier) {
    NavHost(navController = navHostController,modifier=modifier, startDestination = HomeScreen.HOME.route){
        composable(HomeScreen.HOME.route){
            HomeScreenPage()
        }
        composable(HomeScreen.MUSIC.route){
            //SearchScreenPage()
        }
        composable(HomeScreen.SETTINGS.route){
            //SettingsScreenPage()
        }
    }
}