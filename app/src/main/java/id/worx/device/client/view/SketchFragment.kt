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
import id.worx.device.client.R
import id.worx.device.client.navigate
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.screen.main.SketchScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.ThemeViewModelImpl

@AndroidEntryPoint
class SketchFragment : Fragment() {
    private val viewModel by activityViewModels<DetailFormViewModel>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner){ navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.Sketch)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    SketchScreen(viewModel = viewModel) {
                        if (findNavController().previousBackStackEntry?.destination?.id == R.id.photo_preview_fragment){
                            navigate(MainScreen.Detail, MainScreen.Sketch)
                            viewModel.cameraResultUri.value = null
                        } else {
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }
}