package me.mm.auto.audio.list.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SongNotesConverter {
    @TypeConverter
    fun fromSongNotesList(value: List<SongNote>): String {
        val gson = Gson()
        val type = object : TypeToken<List<SongNote>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toSongNotesList(value: String): List<SongNote> {
        val gson = Gson()
        val type = object : TypeToken<List<SongNote>>() {}.type
        return gson.fromJson(value, type)
    }
}
