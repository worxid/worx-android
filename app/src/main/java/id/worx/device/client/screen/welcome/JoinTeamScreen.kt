package id.worx.device.client.screen.welcome

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.text.format.Formatter
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.BuildConfig
import id.worx.device.client.R
import id.worx.device.client.Util
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.JoinTeamForm
import id.worx.device.client.screen.components.RedFullWidthButton
import id.worx.device.client.screen.components.WorxTextField
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.HomeViewModel
import java.util.*

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
    homeViewModel: HomeViewModel,
    onEvent: (JoinTeamEvent) -> Unit
) {
    var namePr by remember { mutableStateOf(0) }
    var orgPr by remember { mutableStateOf(0) }
    val theme = session.theme
    val context = LocalContext.current

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
                .background(MaterialTheme.colors.secondary)
                .verticalScroll(rememberScrollState())
        ) {
            var name by remember { mutableStateOf("") }
            var organization by remember { mutableStateOf("") }

            Text(
                text = stringResource(R.string.ready_to_join),
                style = Typography.h6.copy(MaterialTheme.colors.onSecondary),
                modifier = Modifier.padding(start = 16.dp, top = 40.dp)
            )
            Text(
                text = stringResource(R.string.admin_will_approve),
                style = Typography.body1.copy(MaterialTheme.colors.onSecondary),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 40.dp)
            )
            WorxTextField(
                theme = theme,
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
            WorxTextField(
                theme = theme,
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
            Spacer(modifier = Modifier.weight(1f))
            RedFullWidthButton(
                onClickCallback = {
                    homeViewModel.joinTeam(
                        onSuccess = {
                            onEvent(
                                JoinTeamEvent.JoinTeam(
                                    name,
                                    organization
                                )
                            )
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        },
                        joinTeamForm = provideJoinForm(context, name, organization)
                    )
                },
                label = stringResource(R.string.send_request),
                modifier = Modifier.padding(vertical = 20.dp),
                theme = theme
            )
        }
    }
}

@SuppressLint("HardwareIds")
private fun provideJoinForm(context: Context, name: String, organization: String) : JoinTeamForm{
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE)  as WifiManager
    val ipAddress = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
    return JoinTeamForm(
        device_app_version = BuildConfig.VERSION_CODE.toString(),
        device_code = Util.getDeviceCode(context),
        device_language = Locale.getDefault().language,
        device_model = Build.MANUFACTURER,
        device_os_version = Build.VERSION.SDK_INT.toString(),
        ip = "api.dev.worx.id",
        label = name,
        organization_code = organization,
        port = 443,
    )
    //ip dan port sementara gunakan api.dev.worx.id dan 443
    //org code semenatara WXLK94C
}


@Preview(name = "JoinTeam Screen", showSystemUi = true)
@Composable
fun JoinTeamScreenPreview() {
    WorxTheme {
        JoinTeamScreen(Session(LocalContext.current), hiltViewModel(),{})
    }
}