package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainScreen
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.navigate
import id.worx.device.client.screen.DetailFormEvent
import id.worx.device.client.screen.DetailFormScreen
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailFormFragment : Fragment() {

    private val viewModel by activityViewModels<DetailFormViewModel>()
    private val cameraViewModel by activityViewModels<CameraViewModel>()
    private val scannerViewModel by activityViewModels<ScannerViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModelImpl>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()
    @Inject lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.toastMessage.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { text ->
                Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.Detail)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    DetailFormScreen(
                        viewModel,
                        cameraViewModel,
                        scannerViewModel,
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
                                viewModel.saveFormAsDraft(event.draftDescription) {
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

    private fun observeDownloadForm() {
        WorkManager.getInstance(requireContext()).getWorkInfosForUniqueWorkLiveData("download_form")
            .observe(viewLifecycleOwner) {
                if (it.isNotEmpty() && it[0].state.isFinished) {
                    viewModel.refreshData()
                }
            }
    }
}