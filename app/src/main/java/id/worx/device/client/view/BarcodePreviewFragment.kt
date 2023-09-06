package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import id.worx.device.client.MainScreen
import id.worx.device.client.navigate
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.screen.main.BarcodePreviewScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.ScannerViewModel
import id.worx.device.client.viewmodel.ThemeViewModelImpl

class BarcodePreviewFragment : Fragment() {

    private val scannerViewModel by activityViewModels<ScannerViewModel>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()
    private val viewModel by activityViewModels<DetailFormViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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