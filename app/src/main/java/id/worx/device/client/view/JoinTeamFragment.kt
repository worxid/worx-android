package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.Screen
import id.worx.device.client.navigate
import id.worx.device.client.screen.CreateTeamEvent
import id.worx.device.client.screen.CreateTeamScreen
import id.worx.device.client.screen.JoinTeamEvent
import id.worx.device.client.screen.JoinTeamScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.WelcomeViewModel

@AndroidEntryPoint
class JoinTeamFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, Screen.JoinTeam)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                WorxTheme {
                    JoinTeamScreen(
                        onEvent = { event ->
                            when (event) {
                                is JoinTeamEvent.JoinTeam -> {
                                    viewModel.joinTeam()
                                }
                                JoinTeamEvent.NavigateBack -> {
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