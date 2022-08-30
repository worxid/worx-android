package id.worx.device.client

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.security.InvalidParameterException

enum class WelcomeScreen {
    Splash, Welcome, CreateTeam, CreateTeamSubmitted,
    JoinTeam, WaitingVerification, VerificationRejected }

fun Fragment.navigate(to: WelcomeScreen, from: WelcomeScreen) {
    if (to == from) {
        throw InvalidParameterException("Can't navigate to $to")
    }
    when (to) {
        WelcomeScreen.Welcome -> {
            findNavController().popBackStack(R.id.splash_fragment, true)
            findNavController().navigate(R.id.welcome_fragment)
        }
        WelcomeScreen.CreateTeam -> {
            findNavController().navigate(R.id.create_team_fragment)
        }
        WelcomeScreen.CreateTeamSubmitted -> {
            findNavController().navigate(R.id.create_team_submitted_fragment)
        }
        WelcomeScreen.JoinTeam -> {
            findNavController().navigate(R.id.join_team_fragment)
        }
        WelcomeScreen.WaitingVerification -> {
            findNavController().navigate(R.id.waiting_verification_fragment)
        }
        WelcomeScreen.VerificationRejected -> {
            findNavController().navigate(R.id.verification_rejected_fragment)
        }
        else -> {}
    }
}