import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.mm.sky.auto.music.floatwin.FloatViewModel
import me.mm.sky.auto.music.ui.data.MainScreenViewModel

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
    floatViewModel: FloatViewModel,
    onClick: () -> Unit = {}
) {
    val mainScreenViewModel: MainScreenViewModel = viewModel()
    val uiState = mainScreenViewModel.uiState.collectAsState().value
    val floatState = floatViewModel.floatState.collectAsState().value

    val context = LocalContext.current
    var textState by remember { mutableStateOf(TextFieldValue("耐候")) }

    Surface(
        modifier = Modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Row {
                Text(text = "全局悬浮窗", style = MaterialTheme.typography.titleSmall)
                Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.clickable { onClick() })
            }

            CustomTextField(
                value = textState,
                onValueChange = { textState = it },
                textStyle = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .clickable {
                    }
                    .padding(0.dp)
            )
            LazyColumn {
                items(uiState.settingItems) { settingItem ->
                    Text(text = settingItem.key, Modifier.clickable {
                        Toast.makeText(context, settingItem.key, Toast.LENGTH_SHORT).show()
                    })
                }
            }

        }
    }
}
