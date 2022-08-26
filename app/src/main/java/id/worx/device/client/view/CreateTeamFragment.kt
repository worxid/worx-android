package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.screen.CreateTeamEvent
import id.worx.device.client.screen.CreateTeamScreen
import id.worx.device.client.theme.WorxTheme

@AndroidEntryPoint
class CreateTeamFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                WorxTheme {
                    CreateTeamScreen(
                        onNavigationEvent = { event ->
                            when (event) {
                                is CreateTeamEvent.CreateTeam -> {
                                    //viewModel.signUp(event.email, event.password)
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