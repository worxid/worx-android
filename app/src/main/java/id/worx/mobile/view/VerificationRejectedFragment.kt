package id.worx.mobile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.mobile.data.database.Session
import id.worx.mobile.screen.components.WorxThemeStatusBar
import id.worx.mobile.screen.welcome.VerificationEvent
import id.worx.mobile.screen.welcome.VerificationRejectedScreen
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.ThemeViewModelImpl
import id.worx.mobile.viewmodel.WelcomeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class VerificationRejectedFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()
    private val themeViewModel by viewModels<ThemeViewModelImpl>()

    @Inject lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    VerificationRejectedScreen(
                        session = session,
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