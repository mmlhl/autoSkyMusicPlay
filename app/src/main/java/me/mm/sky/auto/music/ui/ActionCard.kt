package me.mm.sky.auto.music.ui

import androidx.annotation.ColorLong
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


@Composable
fun ActionCard(
    item: ActionCardItem,
    modifier: Modifier = Modifier
) {
    Card (
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp, 4.dp, 8.dp, 4.dp)
            .background(MaterialTheme.colorScheme.surface)
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
                Text(text = stringResource(item.title), fontSize = MaterialTheme.typography.titleMedium.fontSize)
                Text(text = stringResource(item.description), fontSize = MaterialTheme.typography.bodySmall.fontSize, color = MaterialTheme.colorScheme.error)
            }
            Box (
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ){
                Button(onClick = item.onClick) {
                    Text(text = stringResource(item.actionName))
                }
            }
        }

    }
}


data class ActionCardItem(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val actionName: Int,
    val icon: ImageVector,
    val onClick: () -> Unit
) {

}