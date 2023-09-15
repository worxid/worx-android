package id.worx.mobile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import id.worx.mobile.MainScreen
import id.worx.mobile.navigate
import id.worx.mobile.screen.components.WorxThemeStatusBar
import id.worx.mobile.screen.main.BarcodePreviewScreen
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.DetailFormViewModel
import id.worx.mobile.viewmodel.ScannerViewModel
import id.worx.mobile.viewmodel.ThemeViewModelImpl

class BarcodePreviewFragment : Fragment() {

    private val scannerViewModel by activityViewModels<ScannerViewModel>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()
    private val viewModel by activityViewModels<DetailFormViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        scannerViewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.BarcodePreview)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    BarcodePreviewScreen(viewModel, scannerViewModel)
                }
            }
        }
    }
}