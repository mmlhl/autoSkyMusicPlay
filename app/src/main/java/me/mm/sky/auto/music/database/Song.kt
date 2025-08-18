package me.mm.sky.auto.music.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import me.mm.auto.audio.list.database.SongNote

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var author: String,
    var transcribedBy: String,
    val isComposed: Boolean,
    val bpm: Int,
    val bitsPerPage: Int,
    val pitchLevel: Int,
    val isEncrypted: Boolean,
    // 延迟加载的字段设为 nullable，不在初始查询中包含
    @TypeConverters(SongNotesConverter::class)
    val songNotes: List<SongNote>? = null
)