package me.mm.sky.auto.music.ui.musicpage

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.mm.auto.audio.list.database.AppDatabase
import me.mm.auto.audio.list.database.Song
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.ui.CollapsingPageScaffold
import me.mm.sky.auto.music.ui.data.MainScreenViewModel
import me.mm.sky.auto.music.ui.data.music.MusicViewModel


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MusicScreenPage(
    modifier: Modifier = Modifier, data: String = ""
) {


    val dataBase: AppDatabase by lazy { AppDatabase.getInstance(MyContext.context) }

    dataBase.songDao()
    val songViewModel = MusicViewModel
    val mainScreenViewModel: MainScreenViewModel = viewModel()
    val uiState = mainScreenViewModel.uiState.collectAsState().value


    CollapsingPageScaffold(
        title = stringResource(id = uiState.currentScreen.title)

        /*onInitialStatusBarColor = MaterialTheme.colorScheme.background,
        onScrolledStatusBarColor = MaterialTheme.colorScheme.secondary*/

    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            MusicApp(
                songViewModel = songViewModel,
                openEditDialog = remember { mutableStateOf(false) },
                openDeleteDialog = remember { mutableStateOf(false) })
        }

    }


}

@Composable
fun MusicItem(
    song: Song, onClickEdit: () -> Unit, onClickDelete: () -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp  // 设置阴影高度
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .weight(3.0f)
                        .padding(start = 20.dp, end = 0.dp)
                ) {
                    Text(text = song.name)
                    Text(
                        text = "作者:${song.author.ifEmpty { "未知" }}",
                        modifier = Modifier.padding(end = 10.dp)
                    )
                    Text(
                        text = "改编:${song.transcribedBy.ifEmpty { "未知" }}",
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1.0f)) // Use Spacer to occupy remaining space
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .padding(end = 10.dp)
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickEdit() })
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.clickable { onClickDelete() })
                }
            }
        }
    }
}


@Composable
fun MusicApp(
    songViewModel: MusicViewModel,
    openEditDialog: MutableState<Boolean>,
    openDeleteDialog: MutableState<Boolean>
) {
    val nowSong = remember {
        mutableStateOf(
            Song(
                id = 0,
                name = "",
                author = "",
                transcribedBy = "",
                isComposed = false,
                bpm = 0,
                bitsPerPage = 0,
                pitchLevel = 0,
                isEncrypted = false,
                songNotes = emptyList()
            )
        )
    }
    ShowSongList(
        songViewModel = songViewModel,
        openEditDialog = openEditDialog,
        openDeleteDialog = openDeleteDialog,
        nowSong = nowSong,
    )

    if (openEditDialog.value) {
        EditSongDialog(
            openEditDialog = openEditDialog, nowSong = nowSong, songViewModel = songViewModel
        )
    }
    if (openDeleteDialog.value) {
        DeleteSongDialog(
            openDeleteDialog = openDeleteDialog, nowSong = nowSong, songViewModel = songViewModel
        )
    }
}


@Composable
fun ShowSongList(
    songViewModel: MusicViewModel,
    openEditDialog: MutableState<Boolean>,
    openDeleteDialog: MutableState<Boolean>,
    nowSong: MutableState<Song>,
    modifier: Modifier = Modifier
) {
    val songs by songViewModel.songs.collectAsState()

    LazyColumn(modifier = modifier) {
        items(songs) { song ->
            MusicItem(song = song, onClickEdit = {
                openEditDialog.value = true
                nowSong.value = song
            }, onClickDelete = {
                openDeleteDialog.value = true
                nowSong.value = song
            })
        }
    }
}

@Composable
fun EditSongDialog(
    openEditDialog: MutableState<Boolean>,
    nowSong: MutableState<Song>,
    songViewModel: MusicViewModel
) {
    var name by remember { mutableStateOf(nowSong.value.name) }
    var author by remember { mutableStateOf(nowSong.value.author) }
    var transcribedBy by remember { mutableStateOf(nowSong.value.transcribedBy) }

    AlertDialog(
        onDismissRequest = { openEditDialog.value = false },
        title = { Text(text = nowSong.value.name) },
        text = {
            EditSongFields(
                name = name,
                author = author,
                transcribedBy = transcribedBy,
                onNameChange = { name = it },
                onAuthorChange = { author = it },
                onTranscribedByChange = { transcribedBy = it })
        },
        dismissButton = {
            TextButton(onClick = { openEditDialog.value = false }) {
                Text(text = "取消")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                openEditDialog.value = false
                nowSong.value = nowSong.value.copy(
                    name = name, author = author, transcribedBy = transcribedBy
                )
                songViewModel.changeSong(nowSong.value)
            }) {
                Text(text = "保存")
            }
        })
}

@Composable
fun DeleteSongDialog(
    openDeleteDialog: MutableState<Boolean>,
    nowSong: MutableState<Song>,
    songViewModel: MusicViewModel
) {
    AlertDialog(onDismissRequest = {
        openDeleteDialog.value = false
    }, title = { Text(text = nowSong.value.name) }, text = {
        Text(text = "确定删除吗？")
    }, confirmButton = {
        TextButton(onClick = {
            songViewModel.deleteSong(nowSong.value)
            openDeleteDialog.value = false
        }) {
            Text(text = "确定")
        }
    }, dismissButton = {
        TextButton(onClick = { openDeleteDialog.value = false }) {
            Text(text = "取消")
        }
    })
}

@Composable
fun EditSongFields(
    name: String,
    author: String,
    transcribedBy: String,
    onNameChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onTranscribedByChange: (String) -> Unit
) {
    Column {
        EditRow("名称", name, onNameChange)
        EditRow("作者", author, onAuthorChange)
        EditRow("改编", transcribedBy, onTranscribedByChange)
    }
}

@Composable
fun EditRow(
    label: String, value: String, onValueChange: (String) -> Unit
) {
    Row(modifier = Modifier.padding(8.dp)) {
        Text(text = label, modifier = Modifier.align(Alignment.CenterVertically))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .align(Alignment.CenterVertically)
        )
    }
}