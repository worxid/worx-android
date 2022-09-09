package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainScreen
import id.worx.device.client.navigate
import id.worx.device.client.screen.DetailFormScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel

@AndroidEntryPoint
class DetailFormFragment: Fragment() {

    private val viewModel by activityViewModels<DetailFormViewModel>()
    private val cameraViewModel by activityViewModels<CameraViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.Detail)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                WorxTheme {
                    DetailFormScreen(viewModel, cameraViewModel) { activity?.onBackPressedDispatcher?.onBackPressed() }
                }
            }
        }
    }
}