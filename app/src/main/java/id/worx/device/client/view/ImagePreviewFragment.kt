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
import id.worx.device.client.navigate
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.screen.main.ImagePreviewScreen
import id.worx.device.client.screen.main.PunchScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.HomeViewModelImpl

@AndroidEntryPoint
class ImagePreviewFragment : Fragment() {
    private val homeViewModel by activityViewModels<HomeViewModelImpl>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.ImagePreview)
            }
        }
        return ComposeView(requireContext()).apply {
            setContent {
                WorxTheme() {
                    WorxThemeStatusBar()
                    ImagePreviewScreen(homeViewModel = homeViewModel) {
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }
}