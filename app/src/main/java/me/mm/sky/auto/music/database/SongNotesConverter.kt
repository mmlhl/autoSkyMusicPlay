package me.mm.sky.auto.music.database

import android.util.Base64
import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.mm.auto.audio.list.database.SongNote
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class SongNotesConverter {

    private val gson = Gson()
    private val type = object : TypeToken<List<SongNote>>() {}.type

    @TypeConverter
    fun fromSongNotesList(value: List<SongNote>?): String? {
        if (value == null) return null

        return try {
            // 将对象转换为 JSON 字符串
            val jsonString = gson.toJson(value, type)

            // 压缩 JSON 字符串
            val compressedData = compressString(jsonString)

            // 转换为 Base64 字符串存储
            Base64.encodeToString(compressedData, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("SongNotesConverter", "压缩数据时出错", e)
            // 如果压缩失败，回退到未压缩的方式
            gson.toJson(value, type)
        }
    }

    @TypeConverter
    fun toSongNotesList(value: String?): List<SongNote>? {
        if (value.isNullOrEmpty()) return null

        return try {
            // 先尝试作为压缩数据解析
            val compressedData = Base64.decode(value, Base64.DEFAULT)
            val jsonString = decompressString(compressedData)
            gson.fromJson(jsonString, type)
        } catch (e: Exception) {
            // 如果解压失败，尝试作为未压缩的 JSON 解析（兼容旧数据）
            try {
                gson.fromJson(value, type)
            } catch (e2: Exception) {
                Log.e("SongNotesConverter", "解析数据时出错", e2)
                emptyList()
            }
        }
    }

    /**
     * 使用 GZIP 压缩字符串
     */
    private fun compressString(data: String): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        GZIPOutputStream(byteArrayOutputStream).use { gzipOutputStream ->
            gzipOutputStream.write(data.toByteArray(Charsets.UTF_8))
        }
        return byteArrayOutputStream.toByteArray()
    }

    /**
     * 使用 GZIP 解压缩字符串
     */
    private fun decompressString(compressedData: ByteArray): String {
        val byteArrayInputStream = ByteArrayInputStream(compressedData)
        GZIPInputStream(byteArrayInputStream).use { gzipInputStream ->
            return gzipInputStream.readBytes().toString(Charsets.UTF_8)
        }
    }
}