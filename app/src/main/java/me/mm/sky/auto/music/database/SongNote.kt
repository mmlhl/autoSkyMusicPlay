package me.mm.auto.audio.list.database

import android.util.Log
import androidx.room.Ignore

data class SongNote(
    val time: Int,
    val key: String
) {
    val KeyType: String
        get() = key.split("Key").getOrNull(0) ?: ""

    val KeyName: String
        get() = key.split("Key").getOrNull(1) ?: key

    override fun toString(): String {
        return "SongNote(time=$time, key='$key', KeyType='$KeyType', KeyName='$KeyName')"
    }
}
