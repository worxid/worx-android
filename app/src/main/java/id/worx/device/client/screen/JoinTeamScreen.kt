package id.worx.device.client.screen

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.res.stringResource
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

sealed class JoinTeamEvent {
    data class JoinTeam(
        val fullName: String,
        val organizationCode: String
    ) : JoinTeamEvent()

    object NavigateBack : JoinTeamEvent()
}

@Composable
fun JoinTeamScreen(
    onEvent: (JoinTeamEvent) -> Unit
) {
    var namePr by remember { mutableStateOf(0) }
    var orgPr by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            WorxTopAppBar({
                onEvent(JoinTeamEvent.NavigateBack)
            }, progress = namePr + orgPr, "Join An Existing Team")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            var name by remember { mutableStateOf("") }
            var organization by remember { mutableStateOf("") }

            Text(
                text = stringResource(R.string.ready_to_join),
                style = Typography.h6,
            modifier = Modifier.padding(start = 16.dp, top=40.dp))
            Text(
                text = stringResource(R.string.admin_will_approve),
                style = Typography.body1,
                modifier = Modifier.padding(start = 16.dp, top=8.dp, end = 16.dp, bottom = 40.dp))
            WorxTextField(
                label = stringResource(R.string.name),
                KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = {
                    namePr = if (it == "" || it.isEmpty()) {
                        0
                    } else {
                        50
                    }
                    name = it
                }
            )
            WorxTextField(
                label = stringResource(R.string.organization_code),
                KeyboardOptions(keyboardType = KeyboardType.Phone),
                onValueChange = {
                    orgPr = if (it == "" || it.isEmpty()) {
                        0
                    } else {
                        50
                    }
                    organization = it
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            RedFullWidthButton(
                onClickCallback = {
                    onEvent(
                        JoinTeamEvent.JoinTeam(
                            name,
                            organization
                        )
                    )
                },
                label = stringResource(R.string.send_request),
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
    }
}


@Preview(name = "JoinTeam Screen", showSystemUi = true)
@Composable
fun JoinTeamScreenPreview() {
    WorxTheme {
        JoinTeamScreen({})
    }
}