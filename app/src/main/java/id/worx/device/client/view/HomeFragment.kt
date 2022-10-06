package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainScreen
import id.worx.device.client.data.database.Session
import id.worx.device.client.navigate
import id.worx.device.client.screen.WorxThemeStatusBar
import id.worx.device.client.screen.main.HomeScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModel
import id.worx.device.client.viewmodel.ThemeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private val viewModel by activityViewModels<HomeViewModel>()
    private val detailViewModel by activityViewModels<DetailFormViewModel>()
    private val themeViewModel by activityViewModels<ThemeViewModel>()
    @Inject lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.Home)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    HomeScreen(
                        formList = viewModel.uiState.collectAsState().value.list,
                        draftList = viewModel.uiState.collectAsState().value.drafts,
                        submissionList = viewModel.uiState.collectAsState().value.submission,
                    viewModel = viewModel,
                    detailVM = detailViewModel,
                    session = session
                    )
                }
            }
        }
    }
}