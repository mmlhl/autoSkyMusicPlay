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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.mm.auto.audio.list.database.Song
import me.mm.sky.auto.music.ui.data.music.MusicViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MusicScreenPage(
    modifier: Modifier = Modifier,
    data: String = ""
) {
    val musicViewModel = MusicViewModel
    val songs by musicViewModel.songs.collectAsState()
    LazyColumn (){
        items(songs){song->
            MusicItem(song)
        }
    }


}
@Composable
fun MusicItem(
    song: Song
) {
        Column(
            modifier = Modifier
                .fillMaxWidth().padding(bottom = 10.dp)
        ){
            Text(text = "名称${song.name}",style = MaterialTheme.typography.titleMedium)
            Text(text = "作者${song.author}",style = MaterialTheme.typography.bodySmall)
        }

}

@Composable
fun ShowEditDialog(song: Song) {

}
