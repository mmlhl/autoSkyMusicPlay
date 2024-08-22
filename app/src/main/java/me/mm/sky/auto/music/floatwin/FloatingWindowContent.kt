import android.util.Log
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloseFullscreen
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.petterp.floatingx.FloatingX
import me.mm.sky.auto.music.R
import me.mm.sky.auto.music.floatwin.AutoScrollingOrStaticText
import me.mm.sky.auto.music.floatwin.CustomSeekBar
import me.mm.sky.auto.music.floatwin.FloatSateEnum
import me.mm.sky.auto.music.floatwin.FloatViewModel
import me.mm.sky.auto.music.floatwin.FloatingWindowService
import me.mm.sky.auto.music.ui.data.music.MusicViewModel
import me.mm.sky.auto.music.ui.data.music.PlayState

@Composable
fun CustomTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
    cursorColor: Color = Color.Black,
    lineColor: Color = Color.Black
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        cursorBrush = SolidColor(cursorColor),
        modifier = modifier
            .padding(vertical = 8.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = lineColor,
                    start = androidx.compose.ui.geometry.Offset(0f, y),
                    end = androidx.compose.ui.geometry.Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        decorationBox = { innerTextField ->
            innerTextField()
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun FloatingWindowContent(
    onClick: () -> Unit = {}
) {

    val context = LocalContext.current

    var textState by remember { mutableStateOf(TextFieldValue()) }
    Surface(
        color = Color(255, 255, 255, 0),
    ) {
        Card(
            modifier = Modifier
                .width(150.dp)
                .padding(0.dp, 0.dp, 10.dp, 10.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column {
                AndroidView(
                    factory = { context ->
                        FrameLayout(context).apply {
                            id = R.id.frameLayout
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            )
                            val composeView = ComposeView(context).apply {
                                setContent {
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.inverseOnSurface)
                                            .padding(0.dp, 5.dp)
                                    ) {
                                        val iconSize = 20.dp
                                        Row(modifier = Modifier.align(Alignment.CenterStart)) {

                                            Icon(
                                                imageVector = Icons.Outlined.Settings,
                                                contentDescription = null,
                                                modifier = Modifier.size(iconSize)
                                            )
                                            Icon(
                                                imageVector = Icons.Outlined.MyLocation,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(iconSize)
                                                    .clickable {
                                                        FloatViewModel.updateLocationShowing(true)
                                                    }
                                            )

                                        }
                                        Icon(
                                            imageVector = Icons.Outlined.CloseFullscreen,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .size(iconSize)
                                                .clickable {
                                                    FloatViewModel.updateFloatState(FloatSateEnum.FLOAT_SMALL_ICON)
                                                })
                                    }
                                }
                            }
                            addView(composeView)
                        }
                    },
                    modifier = Modifier
                )


                /*CustomTextField(
                    value = textState,
                    onValueChange = { textState = it },
                    textStyle = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .clickable {
                        }
                        .padding(0.dp)
                )*/

                val musicViewModel: MusicViewModel = MusicViewModel
                val playingSong by musicViewModel.currentPlayingSong.collectAsState()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceDim)
                ) {
                    AutoScrollingOrStaticText(
                        text = if (playingSong == null) {
                            "无歌曲播放"
                        } else {
                            "正在播放 ${playingSong?.name}"
                        },
                        boxWidth = 150.dp,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
                val musicPosition by MusicViewModel.currentNoteIndex.collectAsState()
                val totalLength by MusicViewModel.totalLength.collectAsState()
                var dragging by remember {
                    mutableStateOf(false)
                }
                var playState = PlayState.NONE
                Column (modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)){
                    CustomSeekBar(
                        musicPosition,
                        onProgressChanged ={newPosition ->
                            Log.e("TAG", "FloatingWindowContent: "+newPosition)
                            MusicViewModel.updatePlayProgress(newPosition)
                        },
                        onProgressDragStart = {
                            playState=MusicViewModel.playState.value
                            dragging=true
                            MusicViewModel.pause()
                        },
                        onProgressDragEnd = {
                            dragging=false
                            if (playState==PlayState.PLAYING){
                                MusicViewModel.play()
                            }

                        },
                        totalLength,
                    )
/*
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "当前位置：$musicPosition", modifier = Modifier.align(Alignment.Center))
                    }
*/

                }

                Row(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceDim)
                ) {
                    val playState by musicViewModel.playState.collectAsState()
                    IconButton(onClick = {

                    }) {
                        Icon(imageVector = Icons.Outlined.SkipPrevious, contentDescription = null)
                    }
                    IconButton(onClick = {
                        musicViewModel.onPlayClick()
                    }) {
                        Icon(
                            imageVector = if (playState == PlayState.PLAYING) Icons.Outlined.Pause else {
                                Icons.Outlined.PlayArrow
                            }, contentDescription = null
                        )
                    }
                    IconButton(onClick = {
                        /*TODO*/
                    }) {
                        Icon(imageVector = Icons.Outlined.SkipNext, contentDescription = null)
                    }
                }

                val songs by MusicViewModel.songs.collectAsState()

                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                ) {
                    LazyColumn {
                        items(songs) { song ->
                            Column(modifier = Modifier
                                .clickable {
                                    musicViewModel.play(song,0)
                                }) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color.Black)
                                )
                                LazyRow {
                                    item {
                                        Box(modifier = Modifier.padding(6.dp, 4.dp)) {
                                            Text(text = song.name)
                                        }

                                    }
                                }
//                                AutoScrollingOrStaticText(text = song.name, boxWidth = 150.dp)


                            }

                        }

                    }
                }

            }
        }

    }

}
