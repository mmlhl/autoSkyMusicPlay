package me.mm.sky.auto.music.floatwin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.compose.ui.viewinterop.AndroidView
import android.widget.SeekBar

@Composable
fun CustomSeekBar(
    progress: Int,
    onProgressChanged: (Int) -> Unit,
    onProgressDragStart: () -> Unit,
    onProgressDragEnd: () -> Unit,
    maxProgress: Int,
    modifier:Modifier= Modifier
) {
    AndroidView(
        factory = { context ->
            SeekBar(context).apply {
                max = maxProgress
                setProgress(progress)
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            onProgressChanged(progress)

                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        onProgressDragStart()
                    }
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        onProgressDragEnd()
                    }
                })
            }
        },
        update = { seekBar ->
            seekBar.progress = progress
            seekBar.max=maxProgress
        },
        modifier = modifier.fillMaxWidth()
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSlider(
    progress: Int, // 当前进度（整数）
    onProgressChanged: (Int) -> Unit, // 用户拖动进度条时回调
    maxProgress: Int, // 最大进度值（例如400）
    trackHeight: Dp = 4.dp, // 轨道高度
    thumbRadius: Dp = 6.dp, // 滑块半径
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Slider(
            value = progress.toFloat() / maxProgress,
            onValueChange = { newPosition ->
                onProgressChanged((newPosition * maxProgress).toInt())
            },
            valueRange = 0f..1f, // 设置 Slider 的范围
            steps = maxProgress - 1, // 步进数，使得每一步对应一个整数
            modifier = Modifier
                .fillMaxWidth(),

            thumb = {
                Box(
                    Modifier
                        .size(thumbRadius * 2)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
            },
        )
    }
}