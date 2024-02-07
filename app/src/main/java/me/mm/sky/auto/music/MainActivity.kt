package me.mm.sky.auto.music

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import me.mm.sky.auto.music.service.HolderService
import me.mm.sky.auto.music.service.MyService
import me.mm.sky.auto.music.tools.AccessibilityUtils
import me.mm.sky.auto.music.ui.theme.木木弹琴Theme

class MainActivity : ComponentActivity() {
    override fun onResume() {
        super.onResume()
        if (HolderService.holderService == null) {
            val intent = Intent(this@MainActivity, HolderService::class.java)
            startService(intent)
        }
        if (!MyService.isStart()) {
            try {
                if (AccessibilityUtils.isRooted()) {
                    return
                }
                val builder = AlertDialog.Builder(this)
                builder.setTitle("权限申请").setMessage("本软件需要辅助权限以实现自动点击屏幕")
                    .setCancelable(false)
                    .setNegativeButton(
                        "我先看看"
                    ) { dialogInterface, i -> }
                    .setPositiveButton(
                        "确认"
                    ) { dialogInterface: DialogInterface?, i: Int ->
                        this@MainActivity.startActivity(
                            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        )
                    }
                    .setNeutralButton(
                        "进群了解"
                    ) { dialogInterface, i -> joinQQGroup("SzGQPWDd8JMFfqV9q7ZEGTiSe3DjzEYk") }
                    .show()
            } catch (e: Exception) {
                this.startActivity(Intent(Settings.ACTION_SETTINGS))
                e.printStackTrace()
            }
        }

    }

    fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key"))
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "未安装手Q或安装的版本不支持", Toast.LENGTH_SHORT).show()
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!AccessibilityUtils.isAccessibilityServiceEnabled("me.mm.sky.auto.music/.service.MyService")) {
            AccessibilityUtils.enableAccessibilityService("me.mm.sky.auto.music/.service.MyService")
        }
        requestPermission(this)

        setContent {
            木木弹琴Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Text(text = "nh")
    }

}

@Preview(
    showBackground = true
)
@Composable
fun GreetingPreview() {
    木木弹琴Theme {
        Greeting("Android")
    }
}

fun requestPermission(activity: ComponentActivity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            )
            intent.setData(Uri.parse("package:" + activity.packageName))
            activity.startActivity(intent)
        }
    } else {
        //请求文件读写权限
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                100
            )
        }
    }
}
