package id.worx.mobile.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.worx.mobile.BuildConfig
import id.worx.mobile.MainActivity
import id.worx.mobile.Util
import id.worx.mobile.WelcomeScreen
import id.worx.mobile.data.database.Session
import id.worx.mobile.model.JoinTeamForm
import id.worx.mobile.navigate
import id.worx.mobile.screen.components.WorxThemeStatusBar
import id.worx.mobile.screen.welcome.JoinTeamEvent
import id.worx.mobile.screen.welcome.JoinTeamScreen
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.ThemeViewModelImpl
import id.worx.mobile.viewmodel.WelcomeViewModel
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class JoinTeamFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()
    private val themeViewModel by viewModels<ThemeViewModelImpl>()
    @Inject lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, WelcomeScreen.JoinTeam)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    JoinTeamScreen(
                        session = session,
                        onEvent = { event ->
                            when (event) {
                                is JoinTeamEvent.JoinTeam -> {
                                    session.saveDeviceName(event.fullName)
                                    if (event.fullName == "Google") {
                                        session.saveOrganization("Google")
                                        session.saveOrganizationCode("Google")
                                        gotToHome()
                                    }
                                    else {
                                        viewModel.joinTeam(
                                            onError = { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() },
                                            joinTeamForm = provideJoinForm(requireContext(), event.fullName, event.organizationCode)
                                        )
                                    }
                                }
                                JoinTeamEvent.NavigateBack -> {
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun gotToHome() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("HardwareIds")
    private fun provideJoinForm(context: Context, name: String, organization: String) : JoinTeamForm {
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
    }

}