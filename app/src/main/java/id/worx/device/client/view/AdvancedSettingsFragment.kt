package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.WelcomeScreen
import id.worx.device.client.data.database.Session
import id.worx.device.client.navigate
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.screen.welcome.AdvanceSettingsEvent
import id.worx.device.client.screen.welcome.AdvanceSettingsScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.ThemeViewModelImpl
import id.worx.device.client.viewmodel.WelcomeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AdvancedSettingsFragment : Fragment(), WelcomeViewModel.UIHandler {

    private val viewModel by activityViewModels<WelcomeViewModel>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()

    @Inject
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.uiHandler = this
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, WelcomeScreen.AdvancedSetting)
            }
        }

        return ComposeView(requireActivity()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    AdvanceSettingsScreen(
                        session,
                        onEvent = { event ->
                            when (event) {
                                is AdvanceSettingsEvent.SaveUrl -> viewModel.saveServerUrl(session, event.urlServer)
                                AdvanceSettingsEvent.NavigateBack -> findNavController().navigateUp()
                            }
                        }
                    )
                }
            }
        }
    }

    override fun showToast(text: String) {
        Toast.makeText(this.requireActivity().applicationContext, text, Toast.LENGTH_SHORT).show()
    }

}