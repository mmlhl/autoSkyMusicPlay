package me.mm.sky.auto.music.ui.setting

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.mm.sky.auto.music.ui.data.MainActivityViewModel
enum class SettingType {
    BOOLEAN,
    STRING,
    INT,
    SELECT
}

data class SettingItem(
var key:String,
var type:SettingType,
var value:Any,
@StringRes var title:Int,
@StringRes var description:Int,
val selectItems : List<String> = emptyList()
)
@Composable
fun SettingItemView(item: SettingItem, modifier: Modifier=Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp, 4.dp, 8.dp, 4.dp),
    ){
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(item.title),
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
                Text(
                    text = stringResource(item.description),
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                when (item.type) {
                    SettingType.BOOLEAN -> {
                        Switch(
                            checked = item.value as Boolean,
                            onCheckedChange = { isChecked ->
                                MainActivityViewModel.updateSettingItem(item, isChecked)
                            }
                        )
                    }

                    SettingType.STRING -> {
                        Text(text = item.value as String)
                    }

                    SettingType.INT -> {
                        Text(text = (item.value as Int).toString())
                    }

                    SettingType.SELECT -> {
                        Text(text = item.value as String)
                    }
                }
            }
        }
    }
}