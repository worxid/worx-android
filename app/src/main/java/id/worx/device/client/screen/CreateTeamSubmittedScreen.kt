package id.worx.device.client.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.R


sealed class CreateTeamSubmittedEvent {
    object OpenEmailApp : CreateTeamSubmittedEvent()
    object Resend : CreateTeamSubmittedEvent()
}

@Composable
fun CreateTeamSubmittedScreen(onEvent: (CreateTeamSubmittedEvent) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier.padding(horizontal = 120.dp, vertical = 45.dp),
                painter = painterResource(R.drawable.worx_logo),
                contentDescription = "Worx Logo",
                colorFilter = ColorFilter.tint(color = Color.Black)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.ic_email_cartoon),
                contentDescription = " Email Logo"
            )
            Text(
                "Check Your Email",
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                fontFamily = FontFamily.Monospace,
                color = Color.Black,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            Text(
                "Click the link in your email to",
                style = Typography.body1.copy(Color.Black)
            )
            Text(
                "activate your account",
                style = Typography.body1.copy(Color.Black)
            )
            RedFullWidthButton(
                onClickCallback = { onEvent(CreateTeamSubmittedEvent.OpenEmailApp) },
                label = "Open Email App",
                modifier = Modifier.padding(vertical = 20.dp)
            )
            Row(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    "Didn't receive the link? ",
                    style = Typography.body1.copy(Color.Black)
                )
                Text("Resend",
                    style = Typography.body1.copy(color = PrimaryMain),
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
        CreateTeamSubmittedScreen({})
    }
}