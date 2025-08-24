package me.mm.sky.auto.music.ui.setting

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.zhanghai.compose.preference.SwitchPreference
import me.zhanghai.compose.preference.ProvidePreferenceFlow
import me.zhanghai.compose.preference.ProvidePreferenceLocals

@Composable
fun SettingScreenPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    /** 通用更新 DataStore + 缓存 */
    fun updatePreference(keyName: String, value: Boolean) {
        val key = when (keyName) {
            "hideTask" -> hideTaskKey
            "autoHide" -> autoHideKey
            "rootGranted" -> rootGrantedKey
            else -> return
        }
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[key] = value
            }
        }
        // 更新缓存
        when (keyName) {
            "hideTask" -> SettingObserve.hideTaskEnabled = value
            "autoHide" -> SettingObserve.autoHideEnabled = value
            "rootGranted" -> SettingObserve.rootGrantedEnabled = value
        }
    }

    ProvidePreferenceFlow {
        ProvidePreferenceLocals {
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                item {
                    val hideTask by context.dataStore.data
                        .map { it[hideTaskKey] ?: false }
                        .collectAsState(initial = SettingObserve.hideTaskEnabled)

                    SwitchPreference(
                        value = hideTask,
                        onValueChange = { newValue ->
                            updatePreference("hideTask", newValue)
                        },
                        title = { Text("最近任务隐藏本应用") },
                        summary = { Text(if (hideTask) "已开启" else "已关闭") }
                    )
                }

                item {
                    val autoHide by context.dataStore.data
                        .map { it[autoHideKey] ?: false }
                        .collectAsState(initial = SettingObserve.autoHideEnabled)

                    SwitchPreference(
                        value = autoHide,
                        onValueChange = { newValue ->
                            updatePreference("autoHide", newValue)
                        },
                        title = { Text("非光遇界面自动收起悬浮窗") },
                        summary = { Text(if (autoHide) "已开启" else "已关闭") }
                    )
                }

                item {
                    val rootGranted by context.dataStore.data
                        .map { it[rootGrantedKey] ?: false }
                        .collectAsState(initial = SettingObserve.rootGrantedEnabled)

                    SwitchPreference(
                        value = rootGranted,
                        onValueChange = { newValue ->
                            updatePreference("rootGranted", newValue)
                        },
                        title = { Text("Root授权自动授权无障碍") },
                        summary = { Text(if (rootGranted) "已开启" else "已关闭") }
                    )
                }
            }
        }
    }
}
