package me.mm.sky.auto.music.ui.homepage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color.rgb
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.DoDisturb
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import me.mm.sky.auto.music.context.MyContext.Companion.context
import me.mm.sky.auto.music.floatwin.FloatSateEnum
import me.mm.sky.auto.music.floatwin.FloatViewModel
import me.mm.sky.auto.music.ui.data.MainActivityViewModel
import me.mm.sky.auto.music.ui.data.PermissionData
import me.mm.sky.auto.music.ui.data.PermissionRepository

@SuppressLint("IntentWithNullActionLaunch")
fun joinQQGroup(context: Context, key: String): Boolean {
    val intent = Intent().apply {
        data =
            "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key".toUri()
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
    val mainScreenViewModel: MainActivityViewModel = viewModel()
    val uiState = mainScreenViewModel.uiState.collectAsState().value

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

        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeScreen()

            Spacer(modifier = Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
fun ActionCard(permissionData: PermissionData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 4.dp),
        /*colors = CardDefaults.cardColors(
                    if (permissionData.granted) Color(rgb(102, 187, 106)) else Color(rgb(255, 87, 34))
                )*/
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                Icon(
                    imageVector = if (permissionData.granted) Icons.Outlined.CheckCircleOutline else Icons.Outlined.Dangerous,
                    contentDescription = "permissionData.name${if (permissionData.granted) "已授权" else "未授权"}",
                    tint = if (permissionData.granted) Color(rgb(102, 187, 106)) else Color(
                        rgb(
                            255,
                            87,
                            34
                        )
                    ),
                    modifier = Modifier.size(30.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = permissionData.name,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
                Text(
                    text = permissionData.description,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Box(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Button(onClick = onClick) {
                    Text(text = "跳转")
                }
            }
        }

    }

}

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun HomeScreen() {

    val viewModel = MainActivityViewModel()
    LocalContext.current
    val uiState = viewModel<MainActivityViewModel>().uiState.collectAsState().value
    val floatViewModel = FloatViewModel
    floatViewModel.floatState.collectAsState().value
    val isAllNecessaryPermissionsGranted by PermissionRepository.allNecessaryPermissionsGranted.collectAsState()
    val permissions = PermissionRepository.permissions.collectAsState().value
    val status = when {
        !isAllNecessaryPermissionsGranted -> StartCardStatus.NOT_GRANTED
        !uiState.startStatue -> StartCardStatus.NOT_START
        else -> StartCardStatus.START
    }
    Column {
        val cardData = when (status) {
            StartCardStatus.NOT_GRANTED -> StartCardData(
                title = "未授权",
                des = "请授予下方必要权限",
                details = {
                    Text("当前状态说明您没有授予权限，弹琴软件至少需要悬浮窗和无障碍权限来显示操作浮窗与点击屏幕")
                },
                bgColor = MaterialTheme.colorScheme.error,
                textColor = MaterialTheme.colorScheme.onError,
                onCardClick = {

                })

            StartCardStatus.NOT_START -> StartCardData(
                title = "未启动",
                des = "点击启动",
                details = {
                    Text("所有的必要权限已打开，点击即可启动弹琴")
                },
                icon = Icons.Outlined.DoDisturb,
                bgColor = Color.Gray,
                textColor = Color.White,
                onCardClick = {
                    FloatViewModel.updateFloatState(FloatSateEnum.FLOAT_LIST)
                    viewModel.updateStartStatue(true)
                })

            StartCardStatus.START -> StartCardData(
                title = "已启动",
                des = "正在运行",
                details = {
                    Text("服务正在运行中")
                },
                icon = Icons.Outlined.CheckCircleOutline,
                bgColor = Color(rgb(1, 87, 155)),
                textColor = Color.White,
                onCardClick = {
                    viewModel.updateStartStatue(false)
                    viewModel.stopAllService()
                })
        }
        StartCard(cardData)
        permissions.forEach { it ->
            when (it.name) {
                "悬浮窗" -> {
                    ActionCard(it) {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                }

                "无障碍" -> {
                    ActionCard(it) {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                }

                "通知" -> {
                    ActionCard(it) {
                        val intent = Intent()
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                        intent.putExtra("app_package", context.packageName)
                        intent.putExtra("app_uid", context.applicationInfo.uid)

                    }
                }

                /*"ROOT" -> {
                    ActionCard(it) {
                        PermissionUtils.isRooted()
                    }
                }*/
            }
        }

    }


}

// 启动服务按钮卡片相关
enum class StartCardStatus {
    NOT_GRANTED, NOT_START, START,
}

data class StartCardData(
    val title: String,
    val des: String,
    val details: @Composable () -> Unit,
    val icon: ImageVector = Icons.Outlined.Dangerous,
    val bgColor: Color = Color.Red,
    val textColor: Color = Color.Black,
    val iconColor: Color = Color.White,
    val onCardClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartCard(cardData: StartCardData) {
    val tooltipState = rememberTooltipState()
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = cardData.bgColor,
        ), onClick = {
            cardData.onCardClick()
        }, elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 15.dp, 16.dp, 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterVertically),
                imageVector = cardData.icon,
                contentDescription = cardData.title,
                tint = cardData.iconColor
            )
            Column(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .weight(1f)
            ) {
                Text(cardData.title, fontSize = 17.sp, color = cardData.textColor)
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(cardData.des, fontSize = 15.sp, color = cardData.textColor)
            }
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        tonalElevation = 15.dp,
                        modifier = Modifier.padding(20.dp, 0.dp)
                    ) {
                        cardData.details()
                    }
                },
                state = tooltipState,

                ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "详情",
                    tint = cardData.textColor,
                    modifier = Modifier.size(20.dp)
                )
            }

        }
    }
}

@Preview
@Composable
private fun StartCardPreview() {
    val onclick = remember { mutableStateOf(false) }
    val onCardClick = {
        onclick.value = !onclick.value
    }
    StartCard(
        cardData = if (onclick.value) {
            StartCardData(
                title = "未启动",
                des = "点击启动",
                details = {

                },
                icon = Icons.Outlined.DoDisturb,
                bgColor = Color.Gray,
                textColor = Color.White,
                onCardClick = onCardClick
            )
        } else {
            StartCardData(
                title = "已启动",
                des = "点击停止",
                details = {
                    Text("服务正在运行中")
                },
                icon = Icons.Outlined.CheckCircleOutline,
                bgColor = Color(rgb(1, 87, 155)),
                textColor = Color.White,
                onCardClick = onCardClick
            )
        }
    )
}

