package me.mm.sky.auto.music

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.service.HolderService
import me.mm.sky.auto.music.ui.HomeScreen
import me.mm.sky.auto.music.ui.data.MainActivityViewModel
import me.mm.sky.auto.music.ui.data.PermissionRepository
import me.mm.sky.auto.music.ui.manager.MyNavHost
import me.mm.sky.auto.music.ui.setting.SettingObserve
import me.mm.sky.auto.music.ui.theme.木木弹琴Theme

class MainActivity : ComponentActivity() {
    //    val viewModel: MainActivityViewModel = MainActivityViewModel()
    companion object {
        var activity: ComponentActivity? = null
    }

    override fun onResume() {
        super.onResume()
        if (HolderService.holderService == null) {
            val intent = Intent(this@MainActivity, HolderService::class.java)
            startService(intent)

        }


        PermissionRepository.checkAllPermissionsGranted()

    }


    override fun onPause() {
        super.onPause()

        PermissionRepository.checkAllPermissionsGranted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity = this
        if (HolderService.holderService == null) {
            val intent = Intent(this@MainActivity, HolderService::class.java)
            startService(intent)
        }
        super.onCreate(savedInstanceState)
        SettingObserve.observeSetting(this)
//        requestPermission(this)
        setContent {
            木木弹琴Theme {
                val navController = rememberNavController()
                val systemUiController = rememberSystemUiController()
                val darkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f
                val color = MaterialTheme.colorScheme.background
                SideEffect {
                    // 设置状态栏颜色（和背景一致）
                    systemUiController.setStatusBarColor(
                        color = color, darkIcons = !darkTheme // 浅色背景用深色图标，深色背景用亮色图标
                    )
                }
                MainActivityRootView(navController)
            }
        }

    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityRootView(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                HomeScreen.entries.forEach { screen ->
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val selected = currentRoute == screen.route

                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(stringResource(id = screen.title), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        MyNavHost(
            navHostController = navController,
            modifier = Modifier
                .padding(bottom = padding.calculateBottomPadding())
                .windowInsetsPadding(WindowInsets.statusBars)
        )
    }
}

fun requestPermission(activity: ComponentActivity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            )
            intent.data = ("package:" + activity.packageName).toUri()
            activity.startActivity(intent)
        }
    } else {
        //请求文件读写权限
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 100
            )
        }
    }
}

