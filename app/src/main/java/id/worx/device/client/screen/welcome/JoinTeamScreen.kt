package id.worx.device.client.screen.welcome

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.components.RedFullWidthButton
import id.worx.device.client.screen.components.WorxTextField
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme

sealed class JoinTeamEvent {
    data class JoinTeam(
        val fullName: String,
        val organizationCode: String
    ) : JoinTeamEvent()

    object NavigateBack : JoinTeamEvent()
}

@Composable
fun JoinTeamScreen(
    session: Session,
    onEvent: (JoinTeamEvent) -> Unit
) {
    var namePr by remember { mutableStateOf(0) }
    var orgPr by remember { mutableStateOf(0) }
    val theme = session.theme

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colors.secondary)
                .padding(vertical = 36.dp, horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            var name by remember { mutableStateOf("") }
            var organization by remember { mutableStateOf("") }

            Image(
                painter = painterResource(R.drawable.worx_logo),
                contentDescription = "Worx Logo",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(122.dp, 36.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.join_an_existing_team),
                style = Typography.subtitle1.copy(MaterialTheme.colors.onSecondary),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.text_join_team_fill_form),
                style = Typography.body1.copy(MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)),
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            WorxTextField(
                label = stringResource(R.string.name),
                inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {
                    namePr = if (it == "" || it.isEmpty()) {
                        0
                    } else {
                        50
                    }
                    name = it
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            WorxTextField(
                label = stringResource(R.string.organization_code),
                inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {
                    orgPr = if (it == "" || it.isEmpty()) {
                        0
                    } else {
                        50
                    }
                    organization = it
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            RedFullWidthButton(
                onClickCallback = { onEvent(JoinTeamEvent.JoinTeam(name, organization)) },
                label = stringResource(R.string.join_an_existing_team),
                modifier = Modifier,
                theme = theme
            )
        }
    }
}

@Preview(name = "JoinTeam Screen", showSystemUi = true)
@Composable
fun JoinTeamScreenPreview() {
    WorxTheme {
        JoinTeamScreen(Session(LocalContext.current), {})
    }
}

@Preview(name = "JoinTeam Screen", showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun JoinTeamScreenNightPreview() {
    WorxTheme {
        JoinTeamScreen(Session(LocalContext.current), {})
    }
}