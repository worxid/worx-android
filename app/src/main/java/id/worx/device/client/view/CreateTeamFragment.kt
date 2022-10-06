package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.WelcomeScreen
import id.worx.device.client.data.database.Session
import id.worx.device.client.navigate
import id.worx.device.client.screen.WorxThemeStatusBar
import id.worx.device.client.screen.welcome.CreateTeamEvent
import id.worx.device.client.screen.welcome.CreateTeamScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.ThemeViewModel
import id.worx.device.client.viewmodel.WelcomeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class CreateTeamFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()
    private val themeViewModel by viewModels<ThemeViewModel>()
    @Inject
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, WelcomeScreen.CreateTeam)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    CreateTeamScreen(
                        session = session,
                        onNavigationEvent = { event ->
                            when (event) {
                                is CreateTeamEvent.CreateTeam -> {
                                    viewModel.submitNewTeam()
                                }
                                CreateTeamEvent.NavigateBack -> {
                                    activity?.onBackPressedDispatcher?.onBackPressed()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}