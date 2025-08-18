package me.mm.sky.auto.music.floatwin

import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloseFullscreen
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import me.mm.sky.auto.music.R
import me.mm.sky.auto.music.ui.data.music.MusicViewModel
import me.mm.sky.auto.music.ui.data.music.PlayState

@Composable
fun SmallIconFloat() {
    val floatViewModel = FloatViewModel
    val musicViewModel = MusicViewModel
    val currentIndex by musicViewModel.currentNoteIndex.collectAsState()
    val totalLength by musicViewModel.totalLength.collectAsState()
    val playState by musicViewModel.playState.collectAsState()

    Column {
        AndroidView(factory = {
            context ->
            FrameLayout(context).apply {
                id = R.id.smallIconLocation
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            }

        })
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
        ) {
            CircularProgressIndicator(
                progress = { currentIndex.toFloat() / totalLength.toFloat() },
                modifier = Modifier.size(58.dp),
                color = MaterialTheme.colorScheme.inversePrimary,
                strokeWidth = 4.dp,
            )
            IconButton(
                onClick = {

                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(imageVector = when (playState) {
                    PlayState.PLAYING -> Icons.Outlined.Pause
                    else -> Icons.Outlined.PlayArrow
                }, contentDescription = null,
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    floatViewModel.updateFloatState(FloatSateEnum.FLOAT_LIST)
                                },
                                onTap = {
                                    musicViewModel.onPlayClick()
                                }
                            )
                        }
                )
            }
        }
    }
}

@Preview
@Composable
fun SmallIconFloatPreview() {
    SmallIconFloat()
}
