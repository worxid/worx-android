package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainScreen
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.navigate
import id.worx.device.client.screen.DetailFormEvent
import id.worx.device.client.screen.DetailFormScreen
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModel
import id.worx.device.client.viewmodel.ThemeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class DetailFormFragment : Fragment(), DetailFormViewModel.UIHandler {

    private val viewModel by activityViewModels<DetailFormViewModel>()
    private val cameraViewModel by activityViewModels<CameraViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val themeViewModel by activityViewModels<ThemeViewModel>()
    @Inject lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.uiHandler = this

        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.Detail)
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    DetailFormScreen(
                        viewModel,
                        cameraViewModel,
                        session
                    ) { event ->
                        when (event) {
                            is DetailFormEvent.BackPressed -> {
                                findNavController().navigateUp()
                            }
                            is DetailFormEvent.SubmitForm -> {
                                viewModel.validateSubmitForm {
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



    override fun showToast(text: String) {
        Toast.makeText(requireActivity().applicationContext, text, Toast.LENGTH_SHORT).show()
    }
}