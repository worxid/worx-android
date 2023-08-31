package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainScreen
import id.worx.device.client.data.database.Session
import id.worx.device.client.navigate
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.screen.main.SettingScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.HomeViewModelImpl
import id.worx.device.client.viewmodel.ThemeViewModelImpl
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel by activityViewModels<HomeViewModelImpl>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()

    @Inject
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.Settings)
            }
        }

        return ComposeView(requireActivity()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    SettingScreen(
                        viewModel,
                        session = session,
                        themeViewModel = themeViewModel
                    )
                }
            }
        }
    }

}