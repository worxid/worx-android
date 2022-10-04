package id.worx.device.client.screen.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.screen.RedFullWidthButton
import id.worx.device.client.screen.WhiteFullWidthButton
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme

sealed class WelcomeEvent {
    object CreateTeam : WelcomeEvent()
    object JoinTeam : WelcomeEvent()
    object MainScreen : WelcomeEvent()
}

@Composable
fun WelcomeScreen(onEvent: (WelcomeEvent) -> Unit) {

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            WelcomeHeader()
            CreateTeamButton(onEvent = onEvent)
            JoinTeamButton(onEvent)
            GoToMainScreen(onEvent)
        }
    }
}

@Composable
private fun WelcomeHeader() {
    Surface(
        modifier = Modifier.wrapContentSize(),
        color = PrimaryMain
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
                    alignment = Alignment.CenterEnd,
                    contentDescription = "Decoration"
                )
                Text(
                    modifier = Modifier.padding(12.dp),
                    text = "Hi, Welcome!",
                    style = Typography.subtitle1
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
private fun CreateTeamButton(onEvent: (WelcomeEvent) -> Unit) {
    RedFullWidthButton(
        modifier = Modifier.padding(top = 60.dp, bottom = 16.dp),
        onClickCallback = {
            onEvent(WelcomeEvent.CreateTeam)
        },
        label = "Create New Team"
    )
}


@Composable
private fun JoinTeamButton(onEvent: (WelcomeEvent) -> Unit) {
    WhiteFullWidthButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = "Join Existing Team",
        onClickCallback = {onEvent(WelcomeEvent.JoinTeam)}
    )
}

@Composable
private fun GoToMainScreen(onEvent: (WelcomeEvent) -> Unit) {
    WhiteFullWidthButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = "Temporary Main Screen Button",
        onClickCallback = {onEvent(WelcomeEvent.MainScreen)}
    )
}


@Preview(name = "Welcome light theme", showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    WorxTheme {
        WelcomeScreen({})
    }
}