package me.mm.sky.auto.music.ui.musicpage

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.mm.sky.auto.music.database.AppDatabase
import me.mm.sky.auto.music.database.Song
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.ui.CollapsingPageScaffold
import me.mm.sky.auto.music.ui.data.MainActivityViewModel
import me.mm.sky.auto.music.ui.data.music.MusicViewModel

sealed class MusicDialogState {
    object None : MusicDialogState()
    data class Edit(val song: Song) : MusicDialogState()
    data class Delete(val song: Song) : MusicDialogState()
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MusicScreenPage(
    modifier: Modifier = Modifier,
) {
    var dialogState by remember { mutableStateOf<MusicDialogState>(MusicDialogState.None) }

    val mainScreenViewModel: MainActivityViewModel = MyContext.viewModel
    val uiState = mainScreenViewModel.uiState.collectAsState().value

    val songViewModel = MusicViewModel
    val systemUiController = rememberSystemUiController()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

//    val defaultColor = MaterialTheme.colorScheme.background
//    val scrollFraction = scrollBehavior.state.overlappedFraction
//    val appBarColor = defaultColor.copy(alpha = scrollFraction)

    // 状态栏颜色联动
//    SideEffect {
//        val darkIcons = defaultColor.luminance() > 0.5f
//        systemUiController.setStatusBarColor(
//            color = appBarColor,
//            darkIcons = darkIcons
//        )
//    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = uiState.currentScreen.title)) },
                /*colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appBarColor
                ),*/
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            MusicApp(
                songViewModel = songViewModel,
                onEditClick = { song -> dialogState = MusicDialogState.Edit(song) },
                onDeleteClick = { song -> dialogState = MusicDialogState.Delete(song) }
            )

            when (val state = dialogState) {
                is MusicDialogState.Edit -> EditSongDialog(
                    song = state.song,
                    onDismiss = { dialogState = MusicDialogState.None },
                    onSave = { updatedSong ->
                        songViewModel.changeSong(updatedSong)
                        dialogState = MusicDialogState.None
                    }
                )
                is MusicDialogState.Delete -> DeleteSongDialog(
                    song = state.song,
                    onDismiss = { dialogState = MusicDialogState.None },
                    onConfirmDelete = {
                        songViewModel.deleteSong(state.song)
                        dialogState = MusicDialogState.None
                    }
                )
                else -> {}
            }
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
    onEditClick: (Song) -> Unit,
    onDeleteClick: (Song) -> Unit
) {
    val songs by songViewModel.songs.collectAsState()

    LazyColumn {
        items(songs) { song ->
            MusicItem(
                song = song,
                onClickEdit = { onEditClick(song) },
                onClickDelete = { onDeleteClick(song) }
            )
        }
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
    song: Song,
    onDismiss: () -> Unit,
    onSave: (Song) -> Unit
) {
    var name by remember { mutableStateOf(song.name) }
    var author by remember { mutableStateOf(song.author) }
    var transcribedBy by remember { mutableStateOf(song.transcribedBy) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = song.name) },
        text = {
            EditSongFields(
                name = name,
                author = author,
                transcribedBy = transcribedBy,
                onNameChange = { name = it },
                onAuthorChange = { author = it },
                onTranscribedByChange = { transcribedBy = it }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(song.copy(name = name, author = author, transcribedBy = transcribedBy))
            }) {
                Text("保存")
            }
        }
    )
}

@Composable
fun DeleteSongDialog(
    song: Song,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = song.name) },
        text = { Text("确定删除吗？") },
        confirmButton = {
            TextButton(onClick = onConfirmDelete) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
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