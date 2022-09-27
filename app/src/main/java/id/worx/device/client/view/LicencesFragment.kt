package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.screen.main.LicencesScreen
import id.worx.device.client.theme.WorxTheme

@AndroidEntryPoint
class LicencesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireActivity()).apply {
            setContent {
                WorxTheme() {
                    LicencesScreen(onBackNavigation = { activity?.onBackPressedDispatcher?.onBackPressed() })
                }
            }
        }
    }
}