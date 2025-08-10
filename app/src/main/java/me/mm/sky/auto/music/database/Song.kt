package me.mm.auto.audio.list.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

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
