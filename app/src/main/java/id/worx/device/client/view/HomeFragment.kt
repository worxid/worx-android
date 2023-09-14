package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainScreen
import id.worx.device.client.Util
import id.worx.device.client.data.api.SyncServer.Companion.DOWNLOADFROMSERVER
import id.worx.device.client.data.database.Session
import id.worx.device.client.navigate
import id.worx.device.client.screen.main.HomeScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModelImpl
import id.worx.device.client.viewmodel.ThemeViewModelImpl
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel by activityViewModels<HomeViewModelImpl>()
    private val detailViewModel by activityViewModels<DetailFormViewModel>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()

    @Inject
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.Home)
            }
        }

        observeDownloadForm()

        if (Util.isNetworkAvailable(requireContext())){
            viewModel.getDeviceInfo(session)
            viewModel.updateDeviceInfo(session)
            viewModel.syncWithServer(0)
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    val uiState = viewModel.uiState.collectAsState()

                    HomeScreen(
                        formList = uiState.value.list,
                        draftList = uiState.value.drafts,
                        submissionList = uiState.value.submission,
                        viewModel = viewModel,
                        detailVM = detailViewModel,
                        themeViewModel = themeViewModel,
                        session = session,
                        syncWithServer = { syncWithServer() },
                        selectedSort = uiState.value.selectedSort
                    )
                }
            }
        }
    }

    private fun observeDownloadForm() {
        WorkManager.getInstance(requireContext()).getWorkInfosForUniqueWorkLiveData("download_form")
            .observe(viewLifecycleOwner) {
                if (it.isNotEmpty() && it[0].state.isFinished) {
                    viewModel.updateData()
                }
            }
    }

    private fun syncWithServer() {
        viewModel.syncWithServer(DOWNLOADFROMSERVER)
    }
}