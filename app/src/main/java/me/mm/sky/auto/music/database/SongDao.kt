package me.mm.auto.audio.list.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Insert
    suspend fun insert(song: Song)

    @Update
    suspend fun update(song: Song)

    @Query("DELETE FROM songs WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Int): Song?

    @Query("SELECT * FROM songs")
    fun getAllSongs(): Flow<List<Song>>

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()

    @Query("SELECT EXISTS(SELECT name FROM songs WHERE name=:name)")
    fun existSong(name: String): Int
}
