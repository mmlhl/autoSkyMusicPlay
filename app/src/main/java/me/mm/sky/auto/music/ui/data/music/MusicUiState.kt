package me.mm.sky.auto.music.ui.data.music

import me.mm.auto.audio.list.database.Song

data class MusicUiState(
    var songs:List<Song> = listOf(Song(

        name = "11",
        author = "",
        transcribedBy = "",
        isComposed = false,
        bpm = 0,
        bitsPerPage = 0,
        pitchLevel = 0,
        isEncrypted = false,
        songNotes = emptyList()
    ))
) {

}