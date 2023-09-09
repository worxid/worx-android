package id.worx.device.client.screen.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.components.RedFullWidthButton
import id.worx.device.client.screen.components.WorxTextField
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.DarkBackground
import id.worx.device.client.theme.WorxTheme

sealed class CreateTeamEvent {
    data class CreateTeam(
        val fullName: String,
        val email: String,
        val password: String,
        val workPhone: String,
        val organizationName: String
    ) : CreateTeamEvent()

    object NavigateBack : CreateTeamEvent()
}

@Composable
fun CreateTeamScreen(
    session: Session,
    onNavigationEvent: (CreateTeamEvent) -> Unit
) {
    var namePr by remember { mutableStateOf(0) }
    var emailPr by remember { mutableStateOf(0) }
    var passwordPr by remember { mutableStateOf(0) }
    var phonePr by remember { mutableStateOf(0) }
    var orgPr by remember { mutableStateOf(0) }
    val theme = session.theme

    Scaffold(
        topBar = {
            WorxTopAppBar({
                onNavigationEvent(CreateTeamEvent.NavigateBack)
            }, progress = namePr + emailPr + passwordPr + phonePr + orgPr, "Create New Team")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(if (theme == SettingTheme.Dark) DarkBackground else Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            var name by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var phone by remember { mutableStateOf("") }
            var organization by remember { mutableStateOf("") }

            WorxTextField(
                label = "Full Name",
                inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {
                    namePr = if (it == "" || it.isEmpty()) {
                        0
                    } else {
                        20
                    }
                    name = it
                }
            )
            WorxTextField(
                label = "Email",
                inputType = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = {
                    emailPr = if (it == "" || it.isEmpty()) {
                        0
                    } else {
                        20
                    }
                    email = it
                }
            )
            WorxTextField(
                label = "Password",
                inputType = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = {
                    passwordPr = if (it == "" || it.isEmpty()) {
                        0
                    } else {
                        20
                    }
                    password = it
                },
                isPassword = true
            )
            WorxTextField(
                label = "Work Phone",
                inputType = KeyboardOptions(keyboardType = KeyboardType.Phone),
                onValueChange = {
                    phonePr = if (it == "" || it.isEmpty()) {
                        0
                    } else {
                        20
                    }
                    phone = it
                }
            )
            WorxTextField(
                label = "Organization Name",
                inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {
                    orgPr = if (it == "" || it.isEmpty()) {
                        0
                    } else {
                        20
                    }
                    organization = it
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            RedFullWidthButton(
                onClickCallback = {
                    onNavigationEvent(
                        CreateTeamEvent.CreateTeam(
                            name,
                            email,
                            password,
                            phone,
                            organization
                        )
                    )
                },
                label = "Create New Team",
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
    }
}

@Preview(name = "CreateTeam Screen", showSystemUi = true)
@Composable
fun CreateTeamScreenPreview() {
    WorxTheme {
        CreateTeamScreen(session = Session(LocalContext.current), {})
    }
}