package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.WelcomeScreen
import id.worx.device.client.data.database.Session
import id.worx.device.client.navigate
import id.worx.device.client.screen.welcome.VerificationEvent
import id.worx.device.client.screen.welcome.WaitingVerificationScreen
import id.worx.device.client.theme.LightThemeColorsSystem
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.ThemeViewModel
import id.worx.device.client.viewmodel.WelcomeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class WaitingVerificationFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()
    private val themeViewModel by viewModels<ThemeViewModel>()
    @Inject lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, WelcomeScreen.WaitingVerification)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {

                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight
                val statusBarColor = LightThemeColorsSystem.primaryVariant
                DisposableEffect(systemUiController, useDarkIcons) {
                    systemUiController.setStatusBarColor(Color.Black.copy(0.2f), darkIcons = useDarkIcons)
                    onDispose {
                        systemUiController.setStatusBarColor(statusBarColor)
                    }
                }

                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WaitingVerificationScreen(
                        session,
                        onEvent = { event ->
                            when (event) {
                                is VerificationEvent.BackToJoinRequest -> {
                                    viewModel.backToJoinRequest()
                                }
                                else -> {}
                            }
                        }
                    )
                }
            }
        }
    }
}
