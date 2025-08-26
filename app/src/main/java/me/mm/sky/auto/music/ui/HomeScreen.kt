package me.mm.sky.auto.music.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import me.mm.sky.auto.music.R
import me.mm.sky.auto.music.ui.homepage.HomeScreenPage
import me.mm.sky.auto.music.ui.musicpage.MusicScreenPage
import me.mm.sky.auto.music.ui.setting.SettingScreenPage

enum class HomeScreen(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector,
    private val body: @Composable (NavHostController) -> Unit
) {
    MUSIC(
        "music",
        R.string.music_screen_title,
        Icons.Outlined.Search,
        { navController -> MusicScreenPage(navController) }
    ),
    HOME(
        "home",
        R.string.home_screen_title,
        Icons.Filled.Home,
        { navController -> HomeScreenPage(navController) }
    ),
    SETTINGS(
        "settings",
        R.string.setting_screen_title,
        Icons.Outlined.Settings,
        { navController -> SettingScreenPage(navController) }
    );

    fun context(navController: NavHostController): @Composable () -> Unit {
        return { body(navController) }
    }
}
