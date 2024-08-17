package me.mm.sky.auto.music.floatwin

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AutoScrollingOrStaticText(text: String, boxWidth: Dp,modifier: Modifier=Modifier) {
    val scrollState = rememberScrollState()
    var textWidthPx by remember { mutableFloatStateOf(0f) }
    var boxWidthPx by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    LazyRow (modifier = Modifier.fillMaxWidth()){
        item {
            Text(
                text = text,
                style = TextStyle(fontSize = 16.sp),
                onTextLayout = { textLayoutResult: TextLayoutResult ->
                    textWidthPx = textLayoutResult.size.width.toFloat()
                },
                color = Color.Transparent,
            )

        }
    }
    Box(
        modifier = Modifier
            .width(boxWidth)
            .horizontalScroll(scrollState) // Apply the scroll state to the Box
    ) {
        with(LocalDensity.current) { boxWidth.toPx() }
        if (textWidthPx <= boxWidthPx) {
                Text(text = text, fontSize = 16.sp, color = Color.Black)
        } else {
            LaunchedEffect(textWidthPx, boxWidthPx) {
                coroutineScope.launch {
                    while (true) {
                        val totalScrollDistance = textWidthPx + boxWidthPx
                        val scrollDuration = 5000
                        scrollState.animateScrollTo(
                            value = totalScrollDistance.toInt(),
                            animationSpec = tween(durationMillis = scrollDuration, easing = LinearEasing)
                        )

                        scrollState.scrollTo(0)
                        delay(1000)
                    }
                }
            }

            Text(text = text, fontSize = 16.sp, color = Color.Black)
        }
    }
}
