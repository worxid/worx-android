package id.worx.mobile.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.mobile.MainActivity
import id.worx.mobile.WelcomeScreen
import id.worx.mobile.data.database.Session
import id.worx.mobile.navigate
import id.worx.mobile.screen.components.WorxThemeStatusBar
import id.worx.mobile.screen.welcome.WelcomeEvent
import id.worx.mobile.screen.welcome.WelcomeScreen
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.ThemeViewModelImpl
import id.worx.mobile.viewmodel.WelcomeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

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
                navigate(navigateTo, WelcomeScreen.Welcome)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    WelcomeScreen(
                        onEvent = { event ->
                            when (event) {
                                is WelcomeEvent.CreateTeam -> viewModel.createNewTeam()
                                is WelcomeEvent.JoinTeam -> viewModel.joinExistingTeam()
                                is WelcomeEvent.AdvancedSettings -> viewModel.goToAdvancedSetting()
                                WelcomeEvent.MainScreen -> gotoMainScreen()
                            }
                        },
                        session
                    )
                }
            }
        }
    }

    private fun gotoMainScreen() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
}
