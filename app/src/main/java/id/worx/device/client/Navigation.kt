package id.worx.device.client

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.worx.device.client.R
import java.security.InvalidParameterException

enum class Screen { Splash, Welcome, CreateTeam, JoinTeam, Survey }

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
//        Screen.Survey -> {
//            findNavController().navigate(R.id.survey_fragment)
//        }
        else -> {}
    }
}