package me.mm.sky.auto.music.ui.homepage

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.mm.sky.auto.music.R
import me.mm.sky.auto.music.service.HolderService
import me.mm.sky.auto.music.ui.ActionCard
import me.mm.sky.auto.music.ui.ActionCardItem
import me.mm.sky.auto.music.ui.data.MainScreenViewModel
import me.mm.sky.auto.music.ui.data.SettingItem

@Composable
fun HomeScreenPage(
    modifier: Modifier = Modifier,
    data:String=""
) {
    val mainScreenViewModel: MainScreenViewModel = viewModel()
    val uiState = mainScreenViewModel.uiState.collectAsState().value

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (!uiState.isAccGranted) {
            ActionCard(
                ActionCardItem(
                    R.string.Acc_pms_title, R.string.Acc_pms_des,
                    R.string.Acc_pms_action_btm_title, Icons.Default.Add
                ) {
                    //跳转无障碍设置
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    HolderService.holderService?.startActivity(intent)

                }
            )
        }
        if (!uiState.isNotificationGranted) {
            ActionCard(
                ActionCardItem(
                    R.string.notification_pms_title, R.string.notification_pms_des,
                    R.string.notification_pms_action_btm_title, Icons.Default.Add
                ) {
                    val context = HolderService.holderService ?: return@ActionCardItem
                    val intent = Intent()
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS")
                    intent.putExtra("app_package", context.packageName)
                    intent.putExtra("app_uid", context.applicationInfo.uid)
                    intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            )
        }

        uiState.settingItems.forEach { settingItem ->
            ItemView(
                item = settingItem,
                modifier = Modifier.fillMaxWidth(),
                mainScreenViewModel = mainScreenViewModel
            )
        }
    }
}
@Composable
fun ItemView(item: SettingItem, modifier: Modifier, mainScreenViewModel: MainScreenViewModel) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            Text(text = stringResource(item.title), fontSize = MaterialTheme.typography.titleMedium.fontSize)
            Text(text = stringResource(item.description), fontSize = MaterialTheme.typography.bodySmall.fontSize, color = MaterialTheme.colorScheme.error)
        }
        Box (
            modifier = Modifier
                .align(Alignment.CenterVertically)
        ){
            when (item.type) {
                Boolean::class.java -> {
                    Switch(
                        checked = item.value as Boolean,
                        onCheckedChange = { isChecked ->
                            mainScreenViewModel.updateSettingItem(item, isChecked)
                        }
                    )
                }
                String::class.java -> {
                    Text(text = item.value as String)
                }
                Int::class.java -> {
                    Text(text = (item.value as Int).toString())
                }
            }
        }
    }
}