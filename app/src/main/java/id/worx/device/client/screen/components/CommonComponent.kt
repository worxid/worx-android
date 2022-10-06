package id.worx.device.client.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.screen.welcome.WelcomeEvent
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.ThemeViewModel

@Composable
fun RedFullWidthButton(
    onClickCallback: () -> Unit,
    label: String,
    modifier: Modifier,
    theme: String?
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (theme == SettingTheme.Dark || theme == SettingTheme.System) RedDarkButton else MaterialTheme.colors.primary,
            contentColor = Color.White
        ),
        border = BorderStroke(
            1.5.dp,
            color = MaterialTheme.colors.onSecondary
        ),
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
    progress: Int? = null,
    title: String
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back Button")
            }
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
                style = Typography.h6,
            )
            if (progress != null) {
                CircularProgressIndicator(
                    progress = progress / 100.toFloat(),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterEnd)
                        .scale(0.75f),
                    color = Color.White.copy(0.3f),
                    strokeWidth = 3.dp,
                )
            }
        }
    }
}

@Composable
fun WhiteFullWidthButton(
    modifier: Modifier,
    label: String,
    onClickEvent: () -> Unit = {},
    theme: String?,
    onClickCallback: (WelcomeEvent) -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary.copy(0.2f),
            contentColor = if (theme == SettingTheme.Dark) Color.White else MaterialTheme.colors.primary
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colors.onSecondary),
        shape = RoundedCornerShape(1),
        contentPadding = PaddingValues(vertical = 14.dp),
        onClick = {
            onClickCallback(WelcomeEvent.JoinTeam)
            onClickEvent()
        }) {
        Text(text = label, style = Typography.button)
    }
}

@Composable
fun WorxThemeStatusBar(
    theme : String? = null
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    val statusBarColor = if (theme == SettingTheme.Dark) MaterialTheme.colors.secondary else MaterialTheme.colors.primaryVariant

    LaunchedEffect(systemUiController, useDarkIcons) {
        systemUiController.setStatusBarColor(statusBarColor)
    }
}