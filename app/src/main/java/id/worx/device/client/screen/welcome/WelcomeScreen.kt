package id.worx.device.client.screen.welcome

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.RedFullWidthButton
import id.worx.device.client.screen.WhiteFullWidthButton
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.DarkBackground
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import javax.inject.Inject

sealed class WelcomeEvent {
    object CreateTeam : WelcomeEvent()
    object JoinTeam : WelcomeEvent()
    object MainScreen : WelcomeEvent()
}

@Composable
fun WelcomeScreen(onEvent: (WelcomeEvent) -> Unit, session: Session) {
    val theme = session.theme
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.secondary)
                .verticalScroll(rememberScrollState())
        ) {
            WelcomeHeader(session)
            CreateTeamButton(session, onEvent = onEvent)
            JoinTeamButton(session, onEvent)
            GoToMainScreen(session, onEvent)
        }
    }
}

@Composable
private fun WelcomeHeader(session: Session) {
    val theme = session.theme
    Surface(
        modifier = Modifier.wrapContentSize(),
        color = if (theme == SettingTheme.Dark) MaterialTheme.colors.secondary else MaterialTheme.colors.primary
    ) {
        Box() {
            Image(
                modifier = Modifier.height(260.dp),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.grid_bg),
                contentDescription = "Grid Background"
            )
            Column(
                modifier = Modifier.padding(vertical = 26.dp, horizontal = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.ic_star),
                    alignment = Alignment.CenterEnd,
                    contentDescription = "Decoration"
                )
                Image(
                    painter = painterResource(id = R.drawable.worx_logo),
                    contentDescription = "Worx Logo"
                )
                Image(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .scale(scaleX = -1f, scaleY = 1f)
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.ic_star),
                    alignment = Alignment.CenterStart,
                    contentDescription = "Decoration"
                )
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = "Hi, Welcome!",
                    style = Typography.subtitle1,
                    color = Color.White
                )
                Text(
                    text = "Enjoy All The Features Of The App",
                    style = Typography.body1
                )
                Text(
                    text = "Easily & Interactively",
                    style = Typography.body1
                )
                Divider(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp, end = 24.dp),
                    color = Color.White,
                    thickness = 1.5.dp,
                    startIndent = 240.dp
                )
            }
        }
    }
}

@Composable
private fun CreateTeamButton(session: Session, onEvent: (WelcomeEvent) -> Unit) {
    val theme = session.theme
    RedFullWidthButton(
        modifier = Modifier.padding(top = 60.dp, bottom = 16.dp),
        onClickCallback = {
            onEvent(WelcomeEvent.CreateTeam)
        },
        label = "Create New Team",
        theme = theme
    )
}


@Composable
private fun JoinTeamButton(session: Session, onEvent: (WelcomeEvent) -> Unit) {
    val theme = session.theme
    WhiteFullWidthButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = "Join Existing Team",
        theme = theme,
        onClickCallback = { onEvent(WelcomeEvent.JoinTeam) }
    )
}

@Composable
private fun GoToMainScreen(session: Session, onEvent: (WelcomeEvent) -> Unit) {
    val theme = session.theme
    WhiteFullWidthButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = "Temporary Main Screen Button",
        theme = theme,
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