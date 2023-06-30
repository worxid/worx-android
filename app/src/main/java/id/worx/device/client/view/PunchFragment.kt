package id.worx.device.client.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainScreen
import id.worx.device.client.data.database.Session
import id.worx.device.client.navigate
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.screen.main.PunchScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.util.TimerService
import id.worx.device.client.util.getReadableLocation
import id.worx.device.client.viewmodel.HomeViewModelImpl
import id.worx.device.client.viewmodel.PunchViewModel
import id.worx.device.client.viewmodel.ThemeViewModelImpl
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PunchFragment() : Fragment() {
    private val punchViewModel by activityViewModels<PunchViewModel>()
    private val homeViewModel by activityViewModels<HomeViewModelImpl>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()

    @Inject
    lateinit var session: Session

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                homeViewModel.navigateFrom.value = null
                findNavController().navigateUp()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        punchViewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.Punch)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                        PunchScreen(punchViewModel = punchViewModel, username = session.username ?: "John  Doe", location = getCountryName(session = session, requireContext()) ?: "Jakarta"){
                            findNavController().navigateUp()
                            homeViewModel.navigateFrom.value = null
                    }
                }
            }
        }
    }
}