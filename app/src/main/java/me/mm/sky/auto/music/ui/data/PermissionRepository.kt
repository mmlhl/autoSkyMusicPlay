package me.mm.sky.auto.music.ui.data

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.service.MyService
import me.mm.sky.auto.music.tools.PermissionUtils

data class PermissionData(
    val name: String,
    val manifest: String? = null,
    //是否必要权限
    val necessary: Boolean = true,
    val description: String,
    val consequence: String,
    val granted: Boolean,
    val requestPermission: () -> Unit
)

object PermissionRepository {
    private val _allNecessaryPermissionsGranted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val allNecessaryPermissionsGranted: MutableStateFlow<Boolean> = _allNecessaryPermissionsGranted
    private val _permissions = MutableStateFlow<List<PermissionData>>(emptyList())
    val permissions: StateFlow<List<PermissionData>> = _permissions

    init {
        _permissions.value = listOf(
            PermissionData(
                name = "通知",
                manifest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else Manifest.permission.SYSTEM_ALERT_WINDOW,
                necessary = false,
                description = "需要通知权限来快捷操作，以及防止杀后台",
                consequence = "容易被杀后台，必要的应用通知可能无法查看",
                granted = false,
                requestPermission = {

                }),
            PermissionData(
                name = "悬浮窗",
                manifest = Manifest.permission.SYSTEM_ALERT_WINDOW,
                description = "需要悬浮窗权限来在光遇界面显示和操作",
                consequence = "不能在其他应用界面开启弹琴功能",
                granted = false,
                requestPermission = {}),
            PermissionData(
                name = "无障碍",
                description = "需要无障碍权限来进行弹琴点击操作",
                consequence = "无法进行屏幕点击，无法弹琴",
                granted = false,
                requestPermission = {}),
            PermissionData(
                name = "ROOT",
                necessary = false,
                description = "有root可以直接开启无障碍",
                consequence = "需要每次手动打开弹琴界面",
                granted = false,
                requestPermission = {

                }),

            )
        checkAllPermissionsGranted()
    }

    fun updatePermission(name: String, granted: Boolean) {
        _permissions.value = _permissions.value.map {
            if (it.name == name) {
                it.copy(granted = granted)
            } else {
                it
            }
        }
    }

    fun updateAllNecessaryPermissionsGranted() {
        var allNecessaryPermissionsGrantedTemp = true
        _permissions.value.forEach { it ->
            if (it.necessary && !it.granted) {
                allNecessaryPermissionsGrantedTemp = false

            }
        }
        _allNecessaryPermissionsGranted.value = allNecessaryPermissionsGrantedTemp
    }

    fun checkAllPermissionsGranted() {
        //更新系统能够直接获取授权状态的权限
        _permissions.value.map {
            if (it.manifest != null) {
                val granted = ContextCompat.checkSelfPermission(
                    MyContext.context, it.manifest
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                updatePermission(it.name, granted)
            }
        }
        updatePermission("悬浮窗", android.provider.Settings.canDrawOverlays(MyContext.context))
        updatePermission("ROOT", PermissionUtils.isRooted())
        updatePermission("无障碍", MyService.isStart())
        Log.d("TAG", "checkAllPermissionsGranted: ${allNecessaryPermissionsGranted.value}")
        updateAllNecessaryPermissionsGranted()
    }
}