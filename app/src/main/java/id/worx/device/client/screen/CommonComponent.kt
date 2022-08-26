package id.worx.device.client.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import id.worx.device.client.theme.RedDarkButton
import id.worx.device.client.theme.Typography

@Composable
fun RedFullWidthButton(
    onClickCallback: () -> Unit,
    label: String,
    modifier: Modifier
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = RedDarkButton,
            contentColor = Color.White
        ),
        border = BorderStroke(1.5.dp, Color.Black),
        shape = RoundedCornerShape(1),
        contentPadding = PaddingValues(vertical = 14.dp),
        onClick = onClickCallback
    ) {
        Text(text = label, style = Typography.button)
    }
}