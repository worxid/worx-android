package id.worx.device.client.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainScreen
import id.worx.device.client.R
import id.worx.device.client.navigate
import id.worx.device.client.screen.PhotoPreviewScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel

@AndroidEntryPoint
class PhotoPreviewFragment: Fragment() {

    private val viewModel by activityViewModels<CameraViewModel>()
    private val detailViewModel by activityViewModels<DetailFormViewModel>()

    override fun onResume() {
        super.onResume()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = Color.TRANSPARENT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.PhotoPreview)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                WorxTheme {
                    PhotoPreviewScreen(viewModel, detailViewModel)
                }
            }
        }
    }

    override fun onDestroyView() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.status_bar_color, null)
        super.onDestroyView()
    }

}