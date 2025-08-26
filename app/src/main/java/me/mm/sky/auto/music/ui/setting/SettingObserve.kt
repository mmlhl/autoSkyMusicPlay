package me.mm.sky.auto.music.ui.setting

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.mm.sky.auto.music.context.MyContext
import me.mm.sky.auto.music.tools.PermissionUtils

// 单例 DataStore
val Context.dataStore by preferencesDataStore(name = "settings")

object SettingObserve {
    private val scope = CoroutineScope(Dispatchers.Default)

    // 内存缓存
    @Volatile
    var hideTaskEnabled: Boolean = false

    @Volatile
    var autoHideEnabled: Boolean = false

    @Volatile
    var rootGrantedEnabled: Boolean = false

    /** 观察 DataStore 并同步更新缓存 */
    fun observeSetting(context: Context) {
        // 最近任务隐藏本应用
        scope.launch {
            context.dataStore.data.map { it[hideTaskKey] ?: false }.collectLatest { enabled ->
                hideTaskEnabled = enabled
                MyContext.hideTask(enabled)
                Log.d("SettingObserve", "HideTask enabled: $enabled")
            }
        }

        // 非光遇界面自动收起悬浮窗
        scope.launch {
            context.dataStore.data.map { it[autoHideKey] ?: false }.collectLatest { enabled ->
                autoHideEnabled = enabled
                Log.d("SettingObserve", "AutoHide enabled: $enabled")
            }
        }

        // 拥有 root 授权自动授权无障碍
        scope.launch {
            context.dataStore.data.map { it[rootGrantedKey] ?: false }.collectLatest { enabled ->
                rootGrantedEnabled = enabled
                Log.d("SettingObserve", "RootGranted enabled: $enabled")
                if (enabled) {
                    PermissionUtils.reAbleAccessibilityService()
                }
            }
        }
    }

    /** 全局读取缓存值，无需阻塞线程 */
    fun isHideTaskEnabled(): Boolean = hideTaskEnabled
    fun isAutoHideEnabled(): Boolean = autoHideEnabled
    fun isRootGrantedEnabled(): Boolean = rootGrantedEnabled
}
