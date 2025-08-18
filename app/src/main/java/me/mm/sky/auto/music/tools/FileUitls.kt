package me.mm.sky.auto.music.tools

import java.io.File
import java.nio.charset.Charset

object FileUtils {
    fun detectEncoding(file: File): Charset {
        val inputStream = file.inputStream()
        val bom = ByteArray(3)
        inputStream.read(bom)
        inputStream.close()

        return when {
            bom[0] == 0xEF.toByte() && bom[1] == 0xBB.toByte() && bom[2] == 0xBF.toByte() -> Charset.forName("UTF-8") // UTF-8 with BOM
            bom[0] == 0xFF.toByte() && bom[1] == 0xFE.toByte() -> Charset.forName("UTF-16LE") // UTF-16 Little Endian
            bom[0] == 0xFE.toByte() && bom[1] == 0xFF.toByte() -> Charset.forName("UTF-16BE") // UTF-16 Big Endian
            else -> Charset.forName("UTF-8") // Default to UTF-8 if no BOM is found
        }
    }
    fun readFileAsString(file: File, charset: Charset): String {
        return file.readText(charset)
    }
    //传入文件路径，返回文件内容
    fun readTextFile(filePath: String): String {
        val file = File(filePath)
        val detectedCharset = detectEncoding(file)
        return readFileAsString(file, detectedCharset)

    }
}