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
import id.worx.device.client.R
import id.worx.device.client.navigate
import id.worx.device.client.screen.DetailFormEvent
import id.worx.device.client.screen.DetailFormScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModel

@AndroidEntryPoint
class DetailFormFragment : Fragment() {

    private val viewModel by activityViewModels<DetailFormViewModel>()
    private val cameraViewModel by activityViewModels<CameraViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()

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
                    DetailFormScreen(
                        viewModel,
                        cameraViewModel
                    ) { event ->
                        when (event) {
                            is DetailFormEvent.BackPressed -> {
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            }
                            is DetailFormEvent.SubmitForm -> {
                                viewModel.submitForm {
                                    homeViewModel.showNotification(1)
                                    homeViewModel.showBadge(R.string.submission)
                                }
                            }
                            is DetailFormEvent.SaveDraft -> {
                                viewModel.saveFormAsDraft {
                                    homeViewModel.showBadge(R.string.draft)
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}