package id.worx.device.client.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.worx.device.client.theme.PrimaryMain
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

@Composable
fun ActionRedButton(
    modifier: Modifier,
    iconRes: Int,
    title: String,
    actionClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(4.dp))
            .background(PrimaryMain.copy(alpha = 0.1f))
            .clickable { actionClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 9.dp),
            painter = painterResource(id = iconRes),
            contentDescription = "Icon",
            tint = PrimaryMain
        )
        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = title,
            style = Typography.body2.copy(PrimaryMain),
        )
    }
}

@Composable
fun WorxTopAppBar(
    onBack: () -> Unit,
    progress: Int,
    title: String
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PrimaryMain,
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = onBack
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back Button")
            }
            Text(
                textAlign = TextAlign.Center,
                text = title,
                style = Typography.h6
            )
            CircularProgressIndicator(
                progress = progress / 100.toFloat(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                color = Color.White.copy(0.3f),
                strokeWidth = 3.dp,
            )
        }
    }
}