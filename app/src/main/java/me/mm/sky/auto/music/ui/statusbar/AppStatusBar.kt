package me.mm.sky.auto.music.ui.statusbar

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController


object AppStatusBar {
    @Composable
    fun SetStatusBarColor(color: Color, darkIcons: Boolean = true) {
        val systemUiController = rememberSystemUiController()
        LaunchedEffect(color, darkIcons) {
            systemUiController.setStatusBarColor(color, darkIcons)
        }
    }


}
