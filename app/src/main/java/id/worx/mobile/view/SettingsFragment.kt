package id.worx.mobile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.mobile.MainScreen
import id.worx.mobile.data.database.Session
import id.worx.mobile.navigate
import id.worx.mobile.screen.components.WorxThemeStatusBar
import id.worx.mobile.screen.main.SettingScreen
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.HomeViewModelImpl
import id.worx.mobile.viewmodel.ThemeViewModelImpl
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
    ): View {

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