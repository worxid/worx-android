package id.worx.device.client.screen.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.components.RedFullWidthButton
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme


sealed class CreateTeamSubmittedEvent {
    object OpenEmailApp : CreateTeamSubmittedEvent()
    object Resend : CreateTeamSubmittedEvent()
}

@Composable
fun CreateTeamSubmittedScreen(session: Session, onEvent: (CreateTeamSubmittedEvent) -> Unit) {
    val theme = session.theme
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(color = MaterialTheme.colors.secondary)
        ) {
            Image(
                modifier = Modifier
                    .padding(vertical = 45.dp)
                    .scale(0.75f),
                painter = painterResource(R.drawable.worx_logo),
                contentDescription = "Worx Logo",
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colors.onSecondary
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = if (theme == SettingTheme.Dark) R.drawable.ic_email_cartoon_yellow else R.drawable.ic_email_cartoon),
                contentDescription = " Email Logo"
            )
            Text(
                "Check Your Email",
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            Text(
                "Click the link in your email to",
                style = Typography.body1.copy(MaterialTheme.colors.onSecondary)
            )
            Text(
                "activate your account",
                style = Typography.body1.copy(MaterialTheme.colors.onSecondary)
            )
            RedFullWidthButton(
                onClickCallback = { onEvent(CreateTeamSubmittedEvent.OpenEmailApp) },
                label = "Open Email App",
                modifier = Modifier.padding(vertical = 20.dp),
                theme = theme
            )
            Row(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    "Didn't receive the link? ",
                    style = Typography.body1.copy(MaterialTheme.colors.onSecondary)
                )
                Text("Resend",
                    style = Typography.body1.copy(color = if (theme == SettingTheme.Dark) PrimaryMain else MaterialTheme.colors.primary),
                    modifier = Modifier.clickable {
                        onEvent(CreateTeamSubmittedEvent.Resend)
                    })
            }
            Spacer(modifier = Modifier.weight(1.5f))
        }
    }
}


@Preview(name = "Submitted Screen", showSystemUi = true)
@Composable
fun SubmittedScreenPreview() {
    WorxTheme {
        CreateTeamSubmittedScreen(Session(LocalContext.current), {})
    }
}