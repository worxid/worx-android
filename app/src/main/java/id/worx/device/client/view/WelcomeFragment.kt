package id.worx.device.client.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainActivity
import id.worx.device.client.WelcomeScreen
import id.worx.device.client.navigate
import id.worx.device.client.screen.welcome.WelcomeEvent
import id.worx.device.client.screen.welcome.WelcomeScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.WelcomeViewModel

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, WelcomeScreen.Welcome)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                WorxTheme {
                    WelcomeScreen(
                        onEvent = { event ->
                            when (event) {
                                is WelcomeEvent.CreateTeam -> viewModel.createNewTeam()
                                is WelcomeEvent.JoinTeam -> viewModel.joinExistingTeam()
                                WelcomeEvent.MainScreen -> gotoMainScreen()
                            }
                        }
                    )
                }
            }
        }
    }

    private fun gotoMainScreen (){
        val intent  = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
}
