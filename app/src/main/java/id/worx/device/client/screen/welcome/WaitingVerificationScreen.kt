package id.worx.device.client.screen.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.components.RedFullWidthButton
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme

sealed class VerificationEvent {
    object MakeNewRequest : VerificationEvent()
    object BackToJoinRequest : VerificationEvent()
}

@Composable
fun WaitingVerificationScreen(
    session: Session, onEvent: (VerificationEvent) -> Unit
) {
    val theme = session.theme
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colors.secondary)
        ) {
            Image(
                modifier = Modifier
                    .padding(vertical = 45.dp)
                    .scale(0.75f),
                painter = painterResource(R.drawable.worx_logo),
                contentDescription = "Worx Logo",
                colorFilter = ColorFilter.tint(color = MaterialTheme.colors.onSecondary)
            )
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = if (theme == SettingTheme.Dark) R.drawable.ic_icon_waiting_dark else R.drawable.ic_icon_waiting),
                contentDescription = "Waiting icon"
            )
            Text(
                stringResource(R.string.waiting_for_verification),
                style = Typography.body1.copy(color = MaterialTheme.colors.onSecondary),
                modifier = Modifier.padding(top = 25.dp, bottom = 20.dp)
            )
            RedFullWidthButton(
                onClickCallback = { onEvent(VerificationEvent.BackToJoinRequest) },
                label = stringResource(R.string.back_to_join_request),
                modifier = Modifier.padding(vertical = 20.dp),
                theme = theme
            )
            Spacer(modifier = Modifier.weight(1.5f))
        }
    }
}


@Preview(name = "Verification Screen", showSystemUi = true)
@Composable
fun VerificationScreenPreview() {
    WorxTheme {
        WaitingVerificationScreen(session = Session(LocalContext.current), {})
    }
}