package me.mm.sky.auto.music.ui.data.music

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.mm.auto.audio.list.database.Song
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.service.MyService
import me.mm.sky.auto.music.sheet.utils.Key

enum class PlayState {
    NONE,
    PLAYING,
    PAUSE,
    STOP
}

@SuppressLint("StaticFieldLeak")
object MusicViewModel : ViewModel() {
    private var myService: MyService? =null
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    private val _playState = MutableStateFlow(PlayState.NONE)
    val playState: StateFlow<PlayState> = _playState
    private val _currentPlayingSong = MutableStateFlow<Song?>(null)
    val currentPlayingSong: StateFlow<Song?> = _currentPlayingSong
    private val songDao = MyContext.database.songDao()
    var songs = _songs.asStateFlow()
    private var _currentNoteIndex = MutableStateFlow(0)
    val currentNoteIndex: StateFlow<Int> = _currentNoteIndex
    private var _totalLength = MutableStateFlow(1)
    val totalLength: StateFlow<Int> = _totalLength


    private val _speed = 1f
    private var job: Job? = null


    private val _dragTime = MutableStateFlow<String>("00:00")
    val dragTime= _dragTime
    init {
        loadSongs()
    }
    fun updatePlayProgress(progress: Int) {
        _currentNoteIndex.value = progress
    }
    fun pause() {
        _playState.value = PlayState.PAUSE
        job?.cancel()
    }
    fun stop() {
        _playState.value = PlayState.STOP
        job?.cancel()
    }

    fun onPlayClick() {
        when (_playState.value) {
            PlayState.NONE -> {
                Toast.makeText(MyContext.context, "未播放歌曲", Toast.LENGTH_SHORT).show()
            }

            PlayState.PLAYING -> {
                _playState.value = PlayState.PAUSE
                pause()
            }

            PlayState.PAUSE -> {
                _playState.value = PlayState.PLAYING
                play()
            }
            PlayState.STOP -> {
                _playState.value = PlayState.PLAYING
                _currentNoteIndex.value=0
                play()
            }
        }
    }

    fun play(song: Song?=_currentPlayingSong.value, index: Int = _currentNoteIndex.value) {
        myService=MyService.myService
        if (song == null) {
            return
        }

        _currentPlayingSong.value = song
        _currentNoteIndex.value = index
        if (song != _currentPlayingSong.value) {
            _currentNoteIndex.value = 0
        }
        _playState.value = PlayState.PLAYING
        var lastTime=0
        val keyMap=Key.keyMap
        if (keyMap.isEmpty()){
            Toast.makeText(MyContext.context, "琴键未初始化,请点击悬浮窗定位按钮进行初始化。", Toast.LENGTH_SHORT).show()
            pause()
            return
        }
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            if (_currentPlayingSong.value != null) {
                val song = songDao.getSongWithNotes(_currentPlayingSong.value!!.id)/*_currentPlayingSong.value!!.songNotes*/
                if (song == null) {
                    return@launch
                }
                val notes = song.songNotes
                if (notes ==null) {
                    return@launch
                }
                _totalLength.value=notes.size
                for (i in _currentNoteIndex.value until  notes.size) {
                    if (isActive) {
                        if (lastTime == 0) {
                            lastTime= notes[i].time
                        }
                        var sleepTime = (notes[i].time * _speed).toLong() - lastTime
                        lastTime = notes[i].time
                        if (sleepTime < 2) {
                            sleepTime = 2
                        }
                        Thread.sleep(sleepTime)
                        _currentNoteIndex.value++
                        keyMap[notes[i].KeyName]?.let { MyService.dispatchGestureClick(it) }
                    } else {
                        pause()
                        break
                    }
                }
                stop()
            }
        }
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch {
            songDao.delete(song.id)
//            loadSongs()
        }
    }
    fun loadSongs() {
        viewModelScope.launch {
            try {
                songDao.getAllSongsWithoutNotes().collect { songList ->
                    _songs.value = songList
                }
            } catch (e: Exception) {
                // 处理获取歌曲列表时的异常
                e.printStackTrace()
            }
        }
    }

    fun changeSong(song: Song) {
        _songs.update { currentSongs ->
            currentSongs.map {
                if (it.id == song.id) song else it
            }
        }
        viewModelScope.launch {
            try {
                songDao.update(song)
                //loadSongs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}