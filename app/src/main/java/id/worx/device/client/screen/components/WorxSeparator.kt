package id.worx.device.client.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.worx.device.client.theme.GrayDivider

@Composable
fun WorxSeparator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Divider(
            color = GrayDivider, modifier = Modifier
                .padding(top = 12.dp)
                .align(Alignment.BottomStart)
        )
    }
}