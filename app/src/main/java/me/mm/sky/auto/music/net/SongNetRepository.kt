package me.mm.sky.auto.music.net

import kotlinx.coroutines.flow.MutableStateFlow

data class SongInfo(
    val id: Long,
    val name: String,
)

object SongNetRepository {
    private var _songListCache= MutableStateFlow<List<SongInfo>>(emptyList())
    public val songList=_songListCache
    private var hasInit=MutableStateFlow<Boolean>(false)
    suspend fun loadSongList(): Unit{
        _songListCache.value=listOf<SongInfo>(
            SongInfo(
                id = 1,
                name = "1"
            ),
            SongInfo(
                id = 2,
                name = "2"
            )
        )
    }
}