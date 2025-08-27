package me.mm.sky.auto.music.ui.musicpage

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.mm.sky.auto.music.net.SongInfo
import me.mm.sky.auto.music.net.SongNetRepository

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AddMusicDialog(
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    scope.launch {
        SongNetRepository.loadSongList()
    }
    val musics by SongNetRepository.songList.collectAsState()

    var selectedSong by remember { mutableStateOf<SongInfo?>(null) }
    var isSearching by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    // 根据搜索过滤
    val filteredMusics = remember(musics, searchText) {
        if (searchText.isBlank()) musics
        else musics.filter { it.name.contains(searchText, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isSearching) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("输入歌曲名...") },
                        singleLine = true
                    )
                } else {
                    Box(modifier = Modifier.weight(1f), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("歌曲列表", style = MaterialTheme.typography.titleLarge)
                    }

                }
                IconButton(onClick = {
                    isSearching = !isSearching
                    if (!isSearching) searchText = "" // 退出搜索时清空
                }) {
                    Icon(Icons.Outlined.Search, contentDescription = "搜索")
                }

            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(filteredMusics, key = { it.id }) { song ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 5.dp)
                            .clickable { selectedSong = song }
                    ) {
                        Text(song.name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        },

    )

    selectedSong?.let { songInfo ->
        DownloadConfirm(info = songInfo, onDismiss = { selectedSong = null })
    }
}

@Composable
fun DownloadConfirm(
    info: SongInfo,
    onDismiss: () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("下载提示") },
        text = { Text("是否下载 ${info.name}?") },
        confirmButton = {
            TextButton(onClick = {
                scope.launch {
                    SongNetRepository.downloadSong(info)
                    onDismiss()
                }
            }) {
                Text("下载")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
