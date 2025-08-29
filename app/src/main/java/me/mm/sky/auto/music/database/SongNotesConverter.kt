package me.mm.sky.auto.music.database

import android.util.Base64
import android.util.Log
import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import me.mm.auto.audio.list.database.SongNote
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class SongNotesConverter {

    // 使用 Kotlinx Serialization 的 Json 实例
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromSongNotesList(value: List<SongNote>?): String? {
        if (value == null) return null
        return try {
            // 转成 JSON 字符串
            val jsonString = json.encodeToString(value)

            // 压缩 JSON 字符串
            val compressedData = compressString(jsonString)

            // 转成 Base64 存储
            Base64.encodeToString(compressedData, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("SongNotesConverter", "压缩数据时出错", e)
            // 如果压缩失败，直接保存未压缩 JSON
            json.encodeToString(value)
        }
    }

    @TypeConverter
    fun toSongNotesList(value: String?): List<SongNote>? {
        if (value.isNullOrEmpty()) return null
        return try {
            // 尝试当作压缩数据解码
            val compressedData = Base64.decode(value, Base64.DEFAULT)
            val jsonString = decompressString(compressedData)
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            // 解压失败，尝试直接解析 JSON（兼容旧数据）
            try {
                json.decodeFromString(value)
            } catch (e2: Exception) {
                Log.e("SongNotesConverter", "解析数据时出错", e2)
                emptyList()
            }
        }
    }

    /** 使用 GZIP 压缩字符串 */
    private fun compressString(data: String): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        GZIPOutputStream(byteArrayOutputStream).use { gzipOutputStream ->
            gzipOutputStream.write(data.toByteArray(Charsets.UTF_8))
        }
        return byteArrayOutputStream.toByteArray()
    }

    /** 使用 GZIP 解压字符串 */
    private fun decompressString(compressedData: ByteArray): String {
        val byteArrayInputStream = ByteArrayInputStream(compressedData)
        GZIPInputStream(byteArrayInputStream).use { gzipInputStream ->
            return gzipInputStream.readBytes().toString(Charsets.UTF_8)
        }
    }
}
