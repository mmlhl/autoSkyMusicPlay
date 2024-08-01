package me.mm.auto.audio.list.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var author: String,
    var transcribedBy: String,
    val isComposed: Boolean,
    val bpm: Int,
    val bitsPerPage: Int,
    val pitchLevel: Int,
    val isEncrypted: Boolean,
    @TypeConverters(SongNotesConverter::class) val songNotes: List<SongNote>
)
