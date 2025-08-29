package me.mm.sky.auto.music.net

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.database.Song

@Serializable
data class SongInfo(
    val id: Long,
    val name: String,
)

sealed class AddMusicStatus {
    object NONE : AddMusicStatus()
    object LOADING : AddMusicStatus()
    object SUCCESS : AddMusicStatus()
    object FAILED : AddMusicStatus()
}

object SongNetRepository {
    private val db = MyContext.database
    private var _songListCache = MutableStateFlow<List<SongInfo>>(emptyList())
    private val _addMusicStatus = MutableStateFlow<AddMusicStatus>(AddMusicStatus.NONE)
    val addMusicStatus = _addMusicStatus
    val songList = _songListCache

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    private const val BASE_URL = "http://43.128.107.237:5742/"
    private var lastModified: String? = null

    suspend fun loadSongList() {
        _addMusicStatus.value = AddMusicStatus.LOADING
        val response: HttpResponse = client.get(BASE_URL + "songs") {
            lastModified?.let {
                header("If-Modified-Since", it)
            }
        }
        if (response.status == HttpStatusCode.NotModified && _songListCache.value.isNotEmpty()) {
            return
        } else {
            val infos: List<SongInfo> = client.get(BASE_URL + "songs").body()
            _songListCache.update { infos }
            lastModified = response.headers[HttpHeaders.LastModified]
        }/*_songListCache.value=(1..10000).map {
            SongInfo(
                id = it.toLong(),
                name = it.toString()
            )
        }*/
        _addMusicStatus.value = AddMusicStatus.SUCCESS
    }

    suspend fun downloadSong(info: SongInfo) {
        val song: Song = client.get("${BASE_URL}song?id=${info.id}").body()
        db.songDao().insert(song)
    }
}