package me.mm.sky.auto.music

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import me.mm.sky.auto.music.context.MyContext.Companion.getIsFloatWindowGranted
import me.mm.sky.auto.music.context.MyContext.Companion.getIsNotificationGranted
import me.mm.sky.auto.music.context.MyContext.Companion.hideTask
import me.mm.sky.auto.music.context.MyContext.Companion.isAccessibilityEnabled
import me.mm.sky.auto.music.context.MyContext.Companion.updateHideTask
import me.mm.sky.auto.music.service.HolderService
import me.mm.sky.auto.music.ui.data.MainActivityViewModel
import me.mm.sky.auto.music.ui.data.PermissionRepository
import me.mm.sky.auto.music.ui.manager.MyNavHost
import me.mm.sky.auto.music.ui.statusbar.AppStatusBar
import me.mm.sky.auto.music.ui.theme.木木弹琴Theme


class MainActivity : ComponentActivity() {
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
        updateHideTask()
        PermissionRepository.checkAllPermissionsGranted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity = this
        if (HolderService.holderService == null) {
            val intent = Intent(this@MainActivity, HolderService::class.java)
            startService(intent)
        }
        super.onCreate(savedInstanceState)

//        requestPermission(this)
        setContent {
            木木弹琴Theme {
                val navController = rememberNavController()
                MainActivityRootView(navController)

            }
        }
        val uiState = MainActivityViewModel.uiState.value

        // 读取配置
        uiState.settingItems.forEach {
            when (it.key) {
                "root_auto_acc" -> {
                    if (it.value as Boolean) {
                        MainActivityViewModel.rootStartService()
                    }
                }

                "hide_task" -> {
                    if (it.value as Boolean) {
                        hideTask(true)
                    }
                }
            }
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityRootView(navController: NavHostController) {
    val mainScreenViewModel: MainActivityViewModel = viewModel()
    val uiState = mainScreenViewModel.uiState.collectAsState().value
    AppStatusBar.SetStatusBarColor(color = MaterialTheme.colorScheme.background, darkIcons = true)
//    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            NavigationBar(
            ) {
                uiState.screens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = null,
                            )
                        },
                        label = {
                            Text(
                                stringResource(id = screen.title),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        selected = uiState.currentScreen == screen,
                        onClick = {
                            if (uiState.currentScreen == screen) {
                                return@NavigationBarItem
                            }
                            mainScreenViewModel.updateCurrentScreen(screen)
                            navController.navigate(screen.route)
                        }
                    )
                }
            }

        },
    ) { padding ->
        MyNavHost(navHostController = navController, modifier = Modifier.padding(padding))
    }
}

fun requestPermission(activity: ComponentActivity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            )
            intent.setData(Uri.parse("package:" + activity.packageName))
            activity.startActivity(intent)
        }
    } else {
        //请求文件读写权限
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                100
            )
        }
    }
}

