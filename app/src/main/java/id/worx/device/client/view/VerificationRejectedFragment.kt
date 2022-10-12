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
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.WorxThemeStatusBar
import id.worx.device.client.screen.welcome.VerificationEvent
import id.worx.device.client.screen.welcome.VerificationRejectedScreen
import id.worx.device.client.theme.LightThemeColorsSystem
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.ThemeViewModel
import id.worx.device.client.viewmodel.WelcomeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class VerificationRejectedFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()
    private val themeViewModel by viewModels<ThemeViewModel>()
    @Inject lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {

                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    VerificationRejectedScreen(
                        session,
                        onEvent = { event ->
                            when (event) {
                                is VerificationEvent.MakeNewRequest -> {
                                    viewModel.makeNewRequest()
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