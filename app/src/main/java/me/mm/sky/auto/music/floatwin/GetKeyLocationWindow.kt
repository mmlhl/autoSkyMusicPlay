package me.mm.sky.auto.music.floatwin

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.petterp.floatingx.FloatingX
import me.mm.sky.auto.music.sheet.utils.Key

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun GetKeyLocationWindow(
) {
    val view = LocalView.current
    val density = LocalDensity.current
    var x0:Int=0
    var y0:Int=0
    var x1:Int=0
    var y1:Int=0
    var clickTimes by remember { mutableIntStateOf(0) }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val locationOnScreen = IntArray(2)
                    view.getLocationOnScreen(locationOnScreen)

                    val x = locationOnScreen[0] + offset.x.toInt()
                    val y = locationOnScreen[1] + offset.y.toInt()
                    if (clickTimes == 0) {
                        x0 = x
                        y0 = y
                        clickTimes++
                    } else {
                        x1 = x
                        y1 = y
                        FloatViewModel.updateLocationShowing(false)
                        Key.init(x0, y0, x1, y1)
                        clickTimes = 0
                    }

                }
            },
        color = Color(120, 180, 120, 120)
    ) {
        Column {
            Box {
                Text(modifier = Modifier.padding(top = 10.dp, start = 5.dp),text = if (clickTimes==0) "请点击屏幕左上角琴键中间位置" else "请点击屏幕右下角琴键中间位置")
            }
            Card(modifier = Modifier, colors = CardDefaults.cardColors(
                contentColor = MaterialTheme.colorScheme.error
            ), onClick = {
                FloatingX.control(tag = "getLocation").hide()
            }
            ) {
                Column {

                    Row {
                        for (i in 0..5) {
                            Box(
                                modifier = Modifier
                                    .padding(5.dp, 5.dp, 5.dp, 0.dp)
                                    .size(10.dp)
                                    .background(
                                        color = if (i == 0 && clickTimes == 0) Color.Red else Color.Gray,
                                        shape = CircleShape
                                    )
                            )
                        }

                    }
                    Row {
                        for (i in 0..5) {
                            Box(
                                modifier = Modifier
                                    .padding(5.dp, 5.dp, 5.dp, 0.dp)
                                    .size(10.dp)
                                    .background(color = Color.Gray, shape = CircleShape)
                            )
                        }

                    }
                    Row {
                        for (i in 0..5) {
                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(10.dp)
                                    .background(
                                        color = if (i == 5 && clickTimes == 1) Color.Red else Color.Gray,
                                        shape = CircleShape
                                    )
                            )
                        }

                    }
                }
            }
            OutlinedButton(onClick = {
                clickTimes=0
                FloatViewModel.updateLocationShowing(false)
            }) {
                Text(text = "取消")
            }
        }
    }
}
