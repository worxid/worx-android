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
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.screen.main.LicencesScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.ThemeViewModelImpl

@AndroidEntryPoint
class LicencesFragment : Fragment() {
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            val theme = themeViewModel.theme.value
            setContent {
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    LicencesScreen(onBackNavigation = { findNavController().navigateUp() })
                }
            }
        }
    }
}