package me.mm.sky.auto.music.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import me.mm.sky.auto.music.R
import me.mm.sky.auto.music.ui.homepage.HomeScreenPage

enum class HomeScreen(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector,
    private val body: @Composable ((String) -> Unit) -> Unit
) {
    MUSIC("music", R.string.music_screen_title, Icons.Outlined.Search, { HomeScreenPage() }),
    HOME("home", R.string.home_screen_title, Icons.Filled.Home, { HomeScreenPage() }),
    SETTINGS("settings", R.string.setting_screen_title, Icons.Outlined.Settings, { HomeScreenPage() });

    fun context(): @Composable ((String) -> Unit) -> Unit {
        return body
    }
}
