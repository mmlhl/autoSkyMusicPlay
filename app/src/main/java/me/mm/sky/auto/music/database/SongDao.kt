package me.mm.sky.auto.music.database

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

    @Query("SELECT id, name, author, transcribedBy, isComposed, bpm, bitsPerPage, pitchLevel, isEncrypted FROM songs")
    fun getAllSongsWithoutNotes(): Flow<List<Song>>

    @Query("SELECT id, name, author, transcribedBy, isComposed, bpm, bitsPerPage, pitchLevel, isEncrypted FROM songs")
    suspend fun getAllSongsWithoutNotesSync(): List<Song>

    /*// 只查询 songNotes
    @Query("SELECT songNotes FROM songs WHERE id = :songId")
     fun getSongNotes(songId: Int): Flow<List<SongNote>>*/

    // 查询完整信息（包含 songNotes）
    @Query("SELECT * FROM songs WHERE id = :songId")
     fun getSongWithNotes(songId: Int): Song?

    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()



    @Query("SELECT EXISTS(SELECT name FROM songs WHERE name=:name)")
    fun existSong(name: String): Int
}
