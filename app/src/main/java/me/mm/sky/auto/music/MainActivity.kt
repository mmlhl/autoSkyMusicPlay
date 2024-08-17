package me.mm.sky.auto.music

import android.Manifest
import android.content.Context
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
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
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.context.MyContext.Companion.getIsFloatWindowGranted
import me.mm.sky.auto.music.context.MyContext.Companion.getIsNotificationGranted
import me.mm.sky.auto.music.context.MyContext.Companion.hideTask
import me.mm.sky.auto.music.context.MyContext.Companion.isAccessibilityEnabled
import me.mm.sky.auto.music.context.MyContext.Companion.updateHideTask
import me.mm.sky.auto.music.service.HolderService
import me.mm.sky.auto.music.service.MyService
import me.mm.sky.auto.music.ui.data.MainScreenViewModel
import me.mm.sky.auto.music.ui.manager.MyNavHost
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
        MainScreenViewModel.updateIsFloatWindowGranted(getIsFloatWindowGranted())
        MainScreenViewModel.updateIsNotificationGranted(
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        )


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

    override fun onPause() {
        super.onPause()
        updateHideTask()
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
        val uiState = MainScreenViewModel.uiState.value

        // 读取配置
        uiState.settingItems.forEach {
            when (it.key) {
                "root_auto_acc" -> {
                    if (it.value as Boolean) {
                        if (!isAccessibilityEnabled() || !MyService.isStart()) {
                            MainScreenViewModel.rootStartService()
                        }
                    }
                }

                "hide_task" -> {
                    if (it.value as Boolean) {
                        hideTask(true)
                    }
                }
            }
        }
        // 根据开启的权限情况，更新HomeScreen内容
        MainScreenViewModel.uiState.value = uiState.copy(
            isFloatWindowGranted = getIsFloatWindowGranted(),
            isNotificationGranted = getIsNotificationGranted(),
            isAccGranted = isAccessibilityEnabled()
        )
    }


}
/****************
 *
 * 发起添加群流程。群号：光遇弹琴辅助(620479828) 的 key 为： B88m46-ejo-5e-Wtr-qgbdTlEVoWJHIK
 * 调用 joinQQGroup("B88m46-ejo-5e-Wtr-qgbdTlEVoWJHIK") 即可发起手Q客户端申请加群 光遇弹琴辅助(620479828)
 *
 * @param key 由官网生成的key
 * @return 返回true表示呼起手Q成功，返回false表示呼起失败
 ******************/
fun joinQQGroup(context: Context, key: String): Boolean {
    val intent = Intent().apply {
        data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
         addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    return try {
        context.startActivity(intent)
        true
    } catch (e: Exception) {
        // 未安装手Q或安装的版本不支持
        false
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
        topBar = {
            val context = LocalContext.current
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

                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(id = R.string.menu)
                        )
                    }
                    IconButton(onClick = {
                        joinQQGroup(context, "B88m46-ejo-5e-Wtr-qgbdTlEVoWJHIK")
                    }){
                        Icon(imageVector = Icons.Outlined.Group, contentDescription = "加群")
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
@Composable
fun PopupExample() {
    var isPopupVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { isPopupVisible = true }) {
            Text("Show Popup")
        }

        if (isPopupVisible) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = { isPopupVisible = false },
                properties = PopupProperties(focusable = true)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.White, shape = RoundedCornerShape(10.dp))
                        .background(Color.Black)
                ) {
                    Text(
                        text = "This is a popup",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
            }
        }
    }
}
