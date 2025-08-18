package me.mm.sky.auto.music.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingPageScaffold(
    title: String,
//    scrollState: LazyListState,
    actions: @Composable RowScope.() -> Unit = {},
    onScrolledStatusBarColor: Color = MaterialTheme.colorScheme.secondary,
    onInitialStatusBarColor: Color = MaterialTheme.colorScheme.background,
    darkIcons: Boolean = true,
    content: @Composable (PaddingValues) -> Unit
) {
//    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    /*// 状态栏控制器
    val systemUiController = rememberSystemUiController()
    val isScrolled = remember(scrollState) {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 0
        }
    }*/

    /*// ✅ 监听 isScrolled，变化时再设置状态栏颜色
    LaunchedEffect(isScrolled.value) {
        systemUiController.setStatusBarColor(
            color = if (isScrolled.value) onScrolledStatusBarColor else onInitialStatusBarColor,
            darkIcons = darkIcons
        )
    }*/

    Scaffold(
        modifier = Modifier/*.nestedScroll(scrollBehavior.nestedScrollConnection)*/, topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }, actions = actions,
//                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = onInitialStatusBarColor,
                    scrolledContainerColor = onScrolledStatusBarColor
                )
            )
        }, content = content
    )
}
