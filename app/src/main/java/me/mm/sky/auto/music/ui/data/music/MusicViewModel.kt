package me.mm.sky.auto.music.ui.data.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.mm.auto.audio.list.database.Song
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.service.HolderService

object MusicViewModel : ViewModel() {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    private val songDao= MyContext.database.songDao()
    var songs = _songs.asStateFlow()
    init {
        getAllSongs()
    }
    fun getAllSongs() {
        viewModelScope.launch {
            try {
                songDao.getAllSongs().collect { songList ->
                    _songs.value=songList
                }
            } catch (e: Exception) {
                // 处理获取歌曲列表时的异常
                e.printStackTrace()
            }
        }
    }
}