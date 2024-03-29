package id.worx.mobile.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.mobile.MainActivity
import id.worx.mobile.Util
import id.worx.mobile.WelcomeScreen
import id.worx.mobile.data.database.Session
import id.worx.mobile.navigate
import id.worx.mobile.screen.components.WorxThemeStatusBar
import id.worx.mobile.screen.welcome.VerificationEvent
import id.worx.mobile.screen.welcome.WaitingVerificationScreen
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.ThemeViewModelImpl
import id.worx.mobile.viewmodel.WelcomeViewModel
import javax.inject.Inject

@AndroidEntryPoint
class WaitingVerificationFragment : Fragment(), WelcomeViewModel.UIHandler {

    private val viewModel by viewModels<WelcomeViewModel>()
    private val themeViewModel by viewModels<ThemeViewModelImpl>()
    @Inject
    lateinit var session: Session

    private var mHandler: Handler? = Handler()

    private var mHandlerTask: Runnable = object : Runnable {
        override fun run() {
            viewModel.getDeviceStatus(session)
            mHandler!!.postDelayed(this, 10*1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.navigateTo.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { navigateTo ->
                navigate(navigateTo, WelcomeScreen.WaitingVerification)
            }
        }

        viewModel.uiHandler = this
        if (Util.isNetworkAvailable(requireContext())) { mHandlerTask.run() }

        viewModel.deviceStatus.observe(viewLifecycleOwner){
            if (it == "APPROVED"){
                gotToHome()
            } else if (it == "REJECTED"){
                goToRejectedScreen()
            }
        }

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    WaitingVerificationScreen(
                        session,
                        onEvent = { event ->
                            when (event) {
                                is VerificationEvent.CancelRequest -> {
                                    viewModel.backToJoinRequest()
                                }
                                else -> {}
                            }
                        }
                    )
                }
            }
        }
    }

    override fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }

    private fun goToRejectedScreen(){
        navigate(WelcomeScreen.VerificationRejected, WelcomeScreen.WaitingVerification)
    }

    private fun gotToHome(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        mHandler?.removeCallbacks(mHandlerTask)
        mHandler = null
        super.onDestroy()
    }
}
