package id.worx.mobile.view

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
import id.worx.mobile.MainScreen
import id.worx.mobile.R
import id.worx.mobile.data.database.Session
import id.worx.mobile.navigate
import id.worx.mobile.screen.components.WorxThemeStatusBar
import id.worx.mobile.screen.main.ScannerScreen
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.DetailFormViewModel
import id.worx.mobile.viewmodel.ScannerViewModel
import id.worx.mobile.viewmodel.ThemeViewModelImpl
import javax.inject.Inject

@AndroidEntryPoint
class ScannerFragment : Fragment() {

    private val viewModel by activityViewModels<ScannerViewModel>()
    private val detailViewModel by activityViewModels<DetailFormViewModel>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()

    @Inject
    lateinit var session: Session

    override fun onResume() {
        super.onResume()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = Color.TRANSPARENT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, MainScreen.ScannerScreen)
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    ScannerScreen(
                        viewModel = detailViewModel,
                        scannerViewModel = viewModel,
                    )
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