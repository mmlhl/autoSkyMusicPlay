package me.mm.sky.auto.music.ui.musicpage

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import me.mm.sky.auto.music.net.SongNetRepository


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
//在music列表弹出的添加音乐的弹窗
fun AddMusicDialog(
    onDismiss: () -> Unit
) {
    val scope= rememberCoroutineScope()
    scope.launch {
        SongNetRepository.loadSongList()
    }
    val musics by SongNetRepository.songList.collectAsState()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Icon(imageVector = Icons.Outlined.Search, contentDescription = "搜索")
        },
        text = {
            Column {
                musics.forEach {
                    Text(it.name)
                }
            }

        },

        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        })
}