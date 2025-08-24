package me.mm.sky.auto.music.ui.setting

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Pages
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import me.mm.sky.auto.music.ui.data.MainActivityViewModel
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.switchPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenPage(modifier: Modifier) {
    ProvidePreferenceLocals {
        LazyColumn(modifier=Modifier.fillMaxSize()) {
            switchPreference(
                key = "switch_preference",
                defaultValue = false,
                title = { Text(text = "Switch preference") },
                icon = { Icon(imageVector = Icons.Outlined.Info, contentDescription = null,modifier = Modifier.clickable(onClick = {
                    Log.e("SettingScreenPage", "SettingScreenPage: onClick")
                })) },
                summary = { Text(text = if (it) "On" else "Off") }
            )
        }

    }
/*    val mainScreenViewModel: MainActivityViewModel = viewModel()
    val uiState = mainScreenViewModel.uiState.collectAsState().value
    val items = uiState.settingItems
    Scaffold(modifier = Modifier, topBar = {
        TopAppBar(modifier = Modifier.fillMaxWidth(), title = {
            Text(
                text = stringResource(id = uiState.currentScreen.title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                )
        })
    }) { padding ->
        Column(modifier = modifier
            .fillMaxSize()
            .padding(padding)) {
            items.forEach {
                SettingItemView(it)
            }
        }

    }*/
}

@Composable
fun SettingScreen() {

}