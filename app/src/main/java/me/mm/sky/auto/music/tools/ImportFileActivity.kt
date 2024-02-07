package me.mm.sky.auto.music.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import me.mm.sky.auto.music.tools.ui.theme.木木弹琴Theme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ImportFileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            木木弹琴Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting2("Android")
                }
            }
        }
        val type = intent.action
        val action = intent.action
        if (type == null || (Intent.ACTION_VIEW != action && Intent.ACTION_SEND != action)) {
            Toast.makeText(this, "意外的打开类型", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center

    ) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    木木弹琴Theme {
        Greeting2("Android")
    }

}

fun getFilePathFromContentUri(
    context: Context,
    uri: Uri,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var data: String? = null

    val filePathColumn =
        arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
    val cursor =
        context.contentResolver.query(uri, filePathColumn, selection, selectionArgs, null)
    cursor?.use { cursor ->
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
            if (index > -1) {
                data = cursor.getString(index)
                if (data == null || !fileIsExists(data!!)) {
                    // 可能获取不到真实路径或文件不存在，执行拷贝流程
                    val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    val fileName = cursor.getString(nameIndex)
                    data = getPathFromInputStreamUri(context, uri, fileName)
                }
            } else {
                // 拷贝一份
                val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val fileName = cursor.getString(nameIndex)
                data = getPathFromInputStreamUri(context, uri, fileName)
            }
        }
    }
    return data
}

/**
 * 使用流拷贝文件到应用私有目录下
 */
fun getPathFromInputStreamUri(context: Context, uri: Uri, fileName: String): String? {
    var inputStream: InputStream? = null
    var filePath: String? = null

    if (uri.authority != null) {
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val file = createTemporalFileFrom(context, inputStream!!, fileName)
            filePath = file.path

        } catch (_: Exception) {
        } finally {
            try {
                inputStream?.close()
            } catch (_: Exception) {
            }
        }
    }

    return filePath
}

@Throws(IOException::class)
fun createTemporalFileFrom(context: Context, inputStream: InputStream, fileName: String): File {
    var targetFile: File?

    inputStream.use { input ->
        // 自定义拷贝文件路径
        targetFile = File(context.getExternalCacheDir(), fileName)
        if (targetFile!!.exists()) {
            targetFile!!.delete()
        }
        val outputStream = FileOutputStream(targetFile!!)

        val buffer = ByteArray(8 * 1024)
        var read: Int
        while (input.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
        outputStream.flush()
        outputStream.close()
    }

    return targetFile!!
}

fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
}

fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
}

fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
}

// 判断文件是否存在
fun fileIsExists(filePath: String): Boolean {
    return try {
        val f = File(filePath)
        f.exists()
    } catch (e: Exception) {
        false
    }
}