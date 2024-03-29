package id.worx.mobile.screen.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.mobile.R
import id.worx.mobile.data.database.Session
import id.worx.mobile.screen.components.RedFullWidthButton
import id.worx.mobile.screen.main.SettingTheme
import id.worx.mobile.theme.PrimaryMain
import id.worx.mobile.theme.Typography
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.theme.fontRoboto

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
                painter = painterResource(R.drawable.worx_logo_red),
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
                stringResource(id = R.string.check_email),
                fontSize = 20.sp,
                fontWeight = FontWeight.W500,
                fontFamily = fontRoboto,
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            Text(
                stringResource(id = R.string.activate_acc_1),
                style = Typography.body1.copy(MaterialTheme.colors.onSecondary)
            )
            Text(
                stringResource(id = R.string.activate_acc_2),
                style = Typography.body1.copy(MaterialTheme.colors.onSecondary)
            )
            RedFullWidthButton(
                onClickCallback = { onEvent(CreateTeamSubmittedEvent.OpenEmailApp) },
                label = stringResource(id = R.string.open_email),
                modifier = Modifier.padding(vertical = 20.dp)
            )
            Row(modifier = Modifier.padding(top = 20.dp)) {
                Text(
                    stringResource(id = R.string.not_receive_link),
                    style = Typography.body1.copy(fontSize = 12.sp, color = MaterialTheme.colors.onSecondary)
                )
                Text(
                    stringResource(id = R.string.resend),
                    style = Typography.body1.copy(fontSize = 12.sp, color = if (theme == SettingTheme.Dark) PrimaryMain else MaterialTheme.colors.primary),
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