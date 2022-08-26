package id.worx.device.client.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.device.client.R
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
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
    onNavigationEvent: (CreateTeamEvent) -> Unit
) {
    var namePr by remember { mutableStateOf(0) }
    var emailPr by remember { mutableStateOf(0) }
    var passwordPr by remember { mutableStateOf(0) }
    var phonePr by remember { mutableStateOf(0) }
    var orgPr by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            WorxTopAppBar({
                onNavigationEvent(CreateTeamEvent.NavigateBack)
            }, progress = namePr + emailPr + passwordPr + phonePr + orgPr)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            var name by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var phone by remember { mutableStateOf("") }
            var organization by remember { mutableStateOf("") }

            WorxTextField(
                label = "Full Name",
                KeyboardOptions(keyboardType = KeyboardType.Text),
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
                KeyboardOptions(keyboardType = KeyboardType.Email),
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
                KeyboardOptions(keyboardType = KeyboardType.Password),
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
                KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                KeyboardOptions(keyboardType = KeyboardType.Phone),
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

@Composable
fun WorxTopAppBar(
    onBack: () -> Unit,
    progress: Int
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PrimaryMain,
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = onBack
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back Button")
            }
            Text(
                textAlign = TextAlign.Center,
                text = "Create New Team",
                style = Typography.h6
            )
            CircularProgressIndicator(
                progress = progress / 100.toFloat(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                color = Color.White.copy(0.3f),
                strokeWidth = 3.dp,
            )
        }
    }
}

@Composable
fun WorxTextField(
    label: String,
    inputType: KeyboardOptions,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    var textValue by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .padding(top = 16.dp)
    ) {
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        Text(
            modifier = Modifier.padding(bottom = 8.dp, start = 17.dp),
            text = label,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = FontFamily.Monospace
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(4.dp),
            value = textValue,
            onValueChange = {
                onValueChange(it.text)
                textValue = it
            },
            label = {
                Text(
                    text = "Enter $label",
                    color = Color.Black.copy(0.54f),
                    fontFamily = FontFamily.Monospace
                )
            },
            textStyle = Typography.body1.copy(color = Color.Black),
            keyboardOptions = inputType,
            visualTransformation = if (isPassword && passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.ic_baseline_visibility_24)
                    else painterResource(id = R.drawable.ic_baseline_visibility_off_24)

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = image, description)
                    }
                }
            }
        )
        Divider(
            color = GrayDivider
        )
    }
}


@Preview(name = "CreateTeam Screen", showSystemUi = true)
@Composable
fun ScreenPreview() {
    WorxTheme {
        CreateTeamScreen({})
    }
}