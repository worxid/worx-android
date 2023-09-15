package id.worx.mobile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.worx.mobile.MainScreen
import id.worx.mobile.navigate
import id.worx.mobile.screen.components.WorxThemeStatusBar
import id.worx.mobile.screen.main.SignaturePadScreen
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.DetailFormViewModel
import id.worx.mobile.viewmodel.ThemeViewModelImpl

@AndroidEntryPoint
class SignaturePadFragment : Fragment() {
    private val viewModel by activityViewModels<DetailFormViewModel>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.SignaturePad)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    SignaturePadScreen(
                        viewModel
                    ) { findNavController().navigateUp() }
                }
            }
        }
    }
}