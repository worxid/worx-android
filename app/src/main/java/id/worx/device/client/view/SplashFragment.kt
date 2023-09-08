package id.worx.device.client.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.MainActivity
import id.worx.device.client.Util
import id.worx.device.client.WelcomeScreen
import id.worx.device.client.data.database.Session
import id.worx.device.client.navigate
import id.worx.device.client.screen.components.WorxThemeStatusBar
import id.worx.device.client.screen.welcome.SplashScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.SplashViewModel
import id.worx.device.client.viewmodel.ThemeViewModelImpl
import javax.inject.Inject

/**
 * Fragment containing the welcome UI.
 */
@AndroidEntryPoint
class SplashFragment : Fragment(), SplashViewModel.UIHandler {

    private val themeViewModel by viewModels<ThemeViewModelImpl>()
    private val splashViewModel by viewModels<SplashViewModel>()
    @Inject lateinit var session : Session

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    SplashScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        splashViewModel.uiHandler = this

        if (Util.isNetworkAvailable(requireContext())) {
            splashViewModel.getDeviceStatus()
        } else {
            splashViewModel.checkDatabase()
        }

        splashViewModel.deviceStatus.observe(viewLifecycleOwner){
            if (it == "APPROVED"){
                gotToHome()
            } else if (it == "PENDING"){
                goToWaitingScreen()
            } else {
                goToWelcomeScreen()
            }
        }
    }

    private fun goToWelcomeScreen(){
        navigate(WelcomeScreen.JoinTeam, WelcomeScreen.Splash)
    }

    private fun goToWaitingScreen(){
        navigate(WelcomeScreen.WaitingVerification, WelcomeScreen.Splash)
    }

    private fun gotToHome(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    override fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }
}
