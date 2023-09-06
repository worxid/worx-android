package id.worx.device.client.screen.welcome

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.components.RedFullWidthButton
import id.worx.device.client.screen.components.WhiteFullWidthButton
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme

sealed class WelcomeEvent {
    object CreateTeam : WelcomeEvent()
    object JoinTeam : WelcomeEvent()
    object MainScreen : WelcomeEvent()
    object AdvancedSettings : WelcomeEvent()
}

@Composable
fun WelcomeScreen(onEvent: (WelcomeEvent) -> Unit, session: Session) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.secondary
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.grid_bg),
                colorFilter = ColorFilter.tint(
                    color = PrimaryMain
                ),
                contentDescription = "Grid Background"
            )
            Column {
                WelcomeHeader(onEvent)
                CreateTeamButton(onEvent = onEvent)
                JoinTeamButton(onEvent)
            }
        }
    }
}

@Composable
private fun WelcomeHeader(onEvent: (WelcomeEvent) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .clickable {
                    onEvent(WelcomeEvent.AdvancedSettings)
                },
            imageVector = Icons.Filled.Settings,
            alignment = Alignment.CenterEnd,
            colorFilter = ColorFilter.tint(PrimaryMain),
            contentDescription = "Settings"
        )
        Image(
            modifier = Modifier.padding(top = 90.dp),
            painter = painterResource(id = R.drawable.worx_logo_red),
            colorFilter = ColorFilter.tint(
                color = PrimaryMain
            ),
            contentDescription = "Worx Logo"
        )
        Row(
            modifier = Modifier.padding(top = 50.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.padding(top = 12.dp, end = 16.dp),
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = "Decoration"
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = stringResource(id = R.string.welcome),
                    style = Typography.subtitle1,
                    color = Color.Black
                )
                Text(
                    text = stringResource(id = R.string.welcome_messg_1),
                    style = Typography.body1
                )
                Text(
                    text = stringResource(id = R.string.welcome_messg_2),
                    style = Typography.body1
                )
                Divider(
                    modifier = Modifier
                        .padding(top = 31.dp)
                        .width(15.dp),
                    color = Color.Black,
                    thickness = 1.5.dp
                )
            }
            Image(
                modifier = Modifier
                    .padding(start = 12.dp, top = 16.dp)
                    .scale(scaleX = -1f, scaleY = 1f),
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = "Decoration"
            )
        }
    }
}

@Composable
private fun CreateTeamButton(onEvent: (WelcomeEvent) -> Unit) {
    RedFullWidthButton(
        modifier = Modifier.padding(top = 60.dp, bottom = 16.dp),
        onClickCallback = {
            onEvent(WelcomeEvent.CreateTeam)
        },
        label = stringResource(id = R.string.create_new_team),
    )
}


@Composable
private fun JoinTeamButton(onEvent: (WelcomeEvent) -> Unit) {
    WhiteFullWidthButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = stringResource(id = R.string.join_team),
        onClickCallback = { onEvent(WelcomeEvent.JoinTeam) }
    )
}

@Composable
private fun GoToMainScreen(onEvent: (WelcomeEvent) -> Unit) {
    WhiteFullWidthButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = "Temporary Main Screen Button",
        onClickCallback = { onEvent(WelcomeEvent.MainScreen) }
    )
}


@Preview(name = "Welcome light theme", showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun WelcomeScreenPreview() {
    WorxTheme {
        WelcomeScreen({}, Session(LocalContext.current))
    }
}

@Preview(name = "Welcome Dark theme", showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WelcomeScreenPreviewDark() {
    WorxTheme(theme = SettingTheme.Dark) {
        WelcomeScreen({}, Session(LocalContext.current))
    }
}