package me.mm.sky.auto.music.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import me.mm.sky.auto.music.ui.data.MainScreenViewModel

@Composable
fun SettingScreenPage() {
    val mainScreenViewModel: MainScreenViewModel = viewModel()
    val uiState = mainScreenViewModel.uiState.collectAsState().value
    val items = uiState.settingItems
    Column{
        items.forEach {
            SettingItemView(it)
        }
    }

}
