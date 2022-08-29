package id.worx.device.client

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.worx.device.client.R
import java.security.InvalidParameterException

enum class Screen { Splash, Welcome, CreateTeam, CreateTeamSubmitted, JoinTeam, WaitingVerification, VerificationRejected }

fun Fragment.navigate(to: Screen, from: Screen) {
    if (to == from) {
        throw InvalidParameterException("Can't navigate to $to")
    }
    when (to) {
        Screen.Welcome -> {
            findNavController().navigate(R.id.welcome_fragment)
        }
        Screen.CreateTeam -> {
            findNavController().navigate(R.id.create_team_fragment)
        }
        Screen.CreateTeamSubmitted -> {
            findNavController().navigate(R.id.create_team_submitted_fragment)
        }
        Screen.JoinTeam -> {
            findNavController().navigate(R.id.join_team_fragment)
        }
        Screen.WaitingVerification -> {
            findNavController().navigate(R.id.waiting_verification_fragment)
        }
        Screen.VerificationRejected -> {
            findNavController().navigate(R.id.verification_rejected_fragment)
        }
        else -> {}
    }
}