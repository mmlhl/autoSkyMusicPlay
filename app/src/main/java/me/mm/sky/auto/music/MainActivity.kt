package me.mm.sky.auto.music

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import me.mm.sky.auto.music.service.HolderService
import me.mm.sky.auto.music.service.MyService
import me.mm.sky.auto.music.tools.AccessibilityUtils
import me.mm.sky.auto.music.ui.HomeScreen
import me.mm.sky.auto.music.ui.data.MainScreenViewModel
import me.mm.sky.auto.music.ui.homepage.HomeScreenPage
import me.mm.sky.auto.music.ui.manager.MyNavHost
import me.mm.sky.auto.music.ui.theme.木木弹琴Theme


class MainActivity : ComponentActivity() {

    override fun onResume() {
        super.onResume()
        MainScreenViewModel.updateIsNotificationGranted(NotificationManagerCompat.from(this).areNotificationsEnabled())
    }

    fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key"))
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "未安装手Q或安装的版本不支持", Toast.LENGTH_SHORT).show()
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (HolderService.holderService == null) {
            val intent = Intent(this@MainActivity, HolderService::class.java)
            startService(intent)
        }
        super.onCreate(savedInstanceState)
//        if (!AccessibilityUtils.isAccessibilityServiceEnabled("me.mm.sky.auto.music/.service.MyService")) {
//            AccessibilityUtils.enableAccessibilityService("me.mm.sky.auto.music/.service.MyService")
//        }
        requestPermission(this)

        setContent {
            木木弹琴Theme {
                val navController = rememberNavController()
                MainActivityRootView(navController)
            }
        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivityRootView(navController: NavHostController) {
    val mainScreenViewModel: MainScreenViewModel = viewModel()
    val uiState = mainScreenViewModel.uiState.collectAsState().value
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            BottomAppBar (

            ){
                NavigationBar(

                ){
                    uiState.screens.forEach { screen ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = null
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
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        stringResource(id = uiState.currentScreen.title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = {
                        //展示一个菜单

                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
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
