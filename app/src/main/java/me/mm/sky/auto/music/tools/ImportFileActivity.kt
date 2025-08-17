package me.mm.sky.auto.music.tools

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
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
import me.mm.sky.auto.music.tools.ui.theme.MyTheme
import me.mm.sky.auto.music.ui.data.MainActivityViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ImportFileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting2("你好，我是木木，qq488803459")
                }
            }
        }

        handleIntent(intent)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        if (action == null || (Intent.ACTION_VIEW != action && Intent.ACTION_SEND != action)) {
            Toast.makeText(this, "意外的打开类型", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        when (action) {
            Intent.ACTION_VIEW -> {
                val uri: Uri? = intent.data
                if (uri != null) {
                    handleFileImport(uri)
                } else {
                    Toast.makeText(this, "无法获取文件", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            Intent.ACTION_SEND -> {
                val uri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM)
                if (uri != null) {
                    handleFileImport(uri)
                } else {
                    Toast.makeText(this, "无法获取文件", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else -> {
                Toast.makeText(this, "意外的打开类型", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun handleFileImport(uri: Uri) {
        val fileName = getFileName(uri)
        if (fileName != null) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val internalFile = File(File(getExternalFilesDir(null),"sheets"), fileName)
                    copyToInternalStorage(inputStream, internalFile)
                    MainActivityViewModel.files2Db()
                    Toast.makeText(this, "文件已保存到私有目录: ${internalFile.absolutePath}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "无法读取文件流", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "保存文件时出错", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "无法获取文件名", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    private fun copyToInternalStorage(inputStream: InputStream, internalFile: File) {
        FileOutputStream(internalFile).use { outputStream ->
            val buffer = ByteArray(4 * 1024)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
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
    MyTheme {
        Greeting2("Android")
    }
}
