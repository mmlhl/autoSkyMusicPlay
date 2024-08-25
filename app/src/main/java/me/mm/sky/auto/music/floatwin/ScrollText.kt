package me.mm.sky.auto.music.floatwin

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

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
    var boxWidthPx by remember { mutableFloatStateOf(boxWidth.value) }
    val coroutineScope = rememberCoroutineScope()
    LazyRow (modifier = Modifier.fillMaxWidth()){
        item {
            Text(
                text = text,
                style = TextStyle(fontSize = 16.sp),
                onTextLayout = { textLayoutResult: TextLayoutResult ->
                    textWidthPx = textLayoutResult.size.width.dp.value
                },
                color = Color.Transparent,
            )

        }
    }
    if (textWidthPx > 0){
        Box(
            modifier = Modifier
                .width(boxWidth)
                .horizontalScroll(scrollState)
        ) {
            if (textWidthPx <= boxWidthPx*3) {

                Text(text = text, fontSize = 16.sp, color = Color.Black,modifier=Modifier.align(Alignment.Center))
            } else {
                LaunchedEffect(textWidthPx, boxWidthPx) {
                    coroutineScope.launch {
                        while (true) {
                            val totalScrollDistance = textWidthPx+15
                            val scrollDuration = 5000
                            scrollState.animateScrollTo(
                                value = totalScrollDistance.toInt(),
                                animationSpec = tween(durationMillis = scrollDuration, easing = LinearEasing)
                            )
                            scrollState.scrollTo(0)
                        }
                    }
                }
                Row {
                    Text(text = text, fontSize = 16.sp, color = Color.Black)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = text, fontSize = 16.sp, color = Color.Black)
                }

            }
        }

    }

}
