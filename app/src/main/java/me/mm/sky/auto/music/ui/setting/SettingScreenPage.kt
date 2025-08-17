package me.mm.sky.auto.music.ui.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenPage(modifier: Modifier) {
    val mainScreenViewModel: MainActivityViewModel = viewModel()
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

    }


}
