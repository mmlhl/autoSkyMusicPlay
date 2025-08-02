package me.mm.sky.auto.music.ui.homepage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DoDisturb
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.mm.sky.auto.music.R
import me.mm.sky.auto.music.context.MyContext.Companion.context
import me.mm.sky.auto.music.floatwin.FloatSateEnum
import me.mm.sky.auto.music.floatwin.FloatViewModel
import me.mm.sky.auto.music.ui.ActionCard
import me.mm.sky.auto.music.ui.ActionCardItem
import me.mm.sky.auto.music.ui.data.MainScreenViewModel
import me.mm.sky.auto.music.ui.setting.SettingItemView
import me.mm.sky.auto.music.ui.statusbar.AppStatusBar

@SuppressLint("IntentWithNullActionLaunch")
fun joinQQGroup(context: Context, key: String): Boolean {
    val intent = Intent().apply {
        data =
            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenPage(
    modifier: Modifier = Modifier, data: String = ""
) {
    val onScrolledStatusBarColor: Color = MaterialTheme.colorScheme.background
    val mainScreenViewModel: MainScreenViewModel = viewModel()
    val uiState = mainScreenViewModel.uiState.collectAsState().value

    val systemUiController = rememberSystemUiController()
    Scaffold(modifier = Modifier, topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(), actions = {
            IconButton(onClick = {
                joinQQGroup(context, "B88m46-ejo-5e-Wtr-qgbdTlEVoWJHIK")
            }) {
                Icon(Icons.Outlined.Group, contentDescription = "加群")
            }
        }, title = {
            Text(
                text = stringResource(id = uiState.currentScreen.title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

                )
        })
    }) { padding ->
//        AppStatusBar.ForceSetStatusBarColor(color = MaterialTheme.colorScheme.background,darkIcons = true)
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!uiState.isAccGranted) {
                ActionCard(ActionCardItem(
                    R.string.Acc_pms_title,
                    R.string.Acc_pms_des,
                    R.string.Acc_pms_action_btm_title,
                    Icons.Default.Add
                ) {
                    //跳转无障碍设置
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)

                })
            }
            if (!uiState.isNotificationGranted) {
                ActionCard(ActionCardItem(
                    R.string.notification_pms_title,
                    R.string.notification_pms_des,
                    R.string.notification_pms_action_btm_title,
                    Icons.Default.Add
                ) {
                    val intent = Intent()
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                    intent.putExtra("app_package", context.packageName)
                    intent.putExtra("app_uid", context.applicationInfo.uid)
                    intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                })
            }
            if (!uiState.isFloatWindowGranted) {
                ActionCard(ActionCardItem(
                    R.string.float_window_pms_title,
                    R.string.float_window_pms_des,
                    R.string.float_window_pms_action_btm_title,
                    Icons.Default.Add
                ) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                })
            }
            if (uiState.isAccGranted && uiState.isFloatWindowGranted && uiState.isNotificationGranted) {/*ActionCard(
                    ActionCardItem(
                        R.string.start_service_title, R.string.start_service_des,
                        R.string.start_service_action, Icons.Default.Add
                    ) {
                        val intent = Intent(context, HolderService::class.java)
                        context.startService(intent)
                    }
                )*/
                StartServiceCard()
            }

            uiState.homeSettingItems.forEach { settingItem ->
                SettingItemView(
                    item = settingItem,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }


}

@Composable
fun StartServiceCard() {
    val context = LocalContext.current
    val uiState = viewModel<MainScreenViewModel>().uiState.collectAsState().value
    val floatViewModel = FloatViewModel
    val floatState = floatViewModel.floatState.collectAsState().value

    Card(modifier = Modifier
        .padding(20.dp, 20.dp)
        .fillMaxWidth(),
        colors = if (uiState.startStatue) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        } else {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        },
        onClick = {
            MainScreenViewModel.updateStartStatue(!uiState.startStatue)
            if (uiState.startStatue) {
                MainScreenViewModel.stopAllService()
            } else {
                FloatViewModel.updateFloatState(FloatSateEnum.FLOAT_LIST)

            }

        }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 16.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Box(Modifier.align(Alignment.CenterVertically)) {
                if (!uiState.startStatue) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .align(Alignment.CenterStart),
                        imageVector = Icons.Default.DoDisturb,
                        contentDescription = stringResource(id = R.string.start_service_title)
                    )
                } else {
                    Icon(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .align(Alignment.CenterStart),
                        imageVector = Icons.Default.CheckCircleOutline,
                        contentDescription = stringResource(id = R.string.stop_service_title)
                    )
                }
            }
            Column {
                Text(
                    text = if (!uiState.startStatue) {
                        stringResource(id = R.string.start_service_title)
                    } else {
                        stringResource(id = R.string.stop_service_title)
                    }, style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.padding(start = 2.dp), text = if (!uiState.startStatue) {
                        stringResource(id = R.string.start_service_des)
                    } else {
                        stringResource(id = R.string.stop_service_des)
                    }, style = MaterialTheme.typography.bodySmall
                )
            }

        }
    }

}