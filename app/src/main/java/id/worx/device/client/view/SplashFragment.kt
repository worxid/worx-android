package id.worx.device.client.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import id.worx.device.client.Screen
import id.worx.device.client.screen.SplashScreen
import id.worx.device.client.navigate
import id.worx.device.client.theme.WorxTheme

/**
 * Fragment containing the welcome UI.
 */
class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                WorxTheme {
                    SplashScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            navigate(Screen.Welcome, Screen.Splash)
        }, 3000)
    }
}
