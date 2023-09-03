package id.worx.device.client.screen.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.components.RedFullWidthButton
import id.worx.device.client.screen.components.WorxTextField
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme

sealed class AdvanceSettingsEvent {
    data class SaveUrl(
        val urlServer: String
    ) : AdvanceSettingsEvent()

    object NavigateBack : AdvanceSettingsEvent()
}

@Composable
fun AdvanceSettingsScreen(
    session: Session,
    onEvent: (AdvanceSettingsEvent) -> Unit
) {
    var progress by remember { mutableStateOf(0) }
    val theme = session.theme

    Scaffold(
        topBar = {
            WorxTopAppBar({
                onEvent(AdvanceSettingsEvent.NavigateBack)
            }, progress = progress, "Advance Settings")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colors.secondary)
                .verticalScroll(rememberScrollState())
        ) {
            var urlServer by remember { mutableStateOf("") }

            Text(
                text = stringResource(R.string.configure_server_url),
                style = Typography.subtitle1.copy(MaterialTheme.colors.onSecondary),
                modifier = Modifier.padding(start = 16.dp, top = 40.dp)
            )
            Text(
                text = stringResource(R.string.configure_server_descr),
                style = Typography.body1.copy(MaterialTheme.colors.onSecondary),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 40.dp)
            )
            WorxTextField(
                label = stringResource(R.string.server_url),
                inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {
                    progress = if (it == "" || it.isEmpty()) {
                        0
                    } else {
                        100
                    }
                   urlServer = it
                }
            )
            Text(
                text = stringResource(id = R.string.example_url),
                style = Typography.body1.copy(fontSize = 12.sp, color = Color.Black.copy(0.6f)),
                modifier = Modifier.padding(start = 28.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            RedFullWidthButton(
                onClickCallback = {onEvent(AdvanceSettingsEvent.SaveUrl(urlServer))},
                label = stringResource(R.string.connect),
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
    }
}

@Preview(name = "AdvanceSettings Screen", showSystemUi = true)
@Composable
fun AdvanceSettingsScreenPreview() {
    WorxTheme {
        AdvanceSettingsScreen(Session(LocalContext.current), {})
    }
}