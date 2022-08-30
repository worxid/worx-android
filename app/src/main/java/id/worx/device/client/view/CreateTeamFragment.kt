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
import id.worx.device.client.navigate
import id.worx.device.client.screen.CreateTeamEvent
import id.worx.device.client.screen.CreateTeamScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.WelcomeViewModel

@AndroidEntryPoint
class CreateTeamFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()

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
                WorxTheme {
                    CreateTeamScreen(
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