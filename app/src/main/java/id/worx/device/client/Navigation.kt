package id.worx.device.client

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import java.security.InvalidParameterException

enum class WelcomeScreen {
    Splash, Welcome, CreateTeam, CreateTeamSubmitted,
    JoinTeam, WaitingVerification, VerificationRejected }

enum class MainScreen {
    Home, Detail, CameraPhoto, PhotoPreview
}

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

fun Fragment.navigate(to: MainScreen, from: MainScreen) {
    if (to == from) {
        throw InvalidParameterException("Can't navigate to $to")
    }
    when (to) {
        MainScreen.Home -> {
            findNavController().navigate(R.id.home_fragment)
        }
        MainScreen.Detail -> {
            findNavController().navigate(R.id.detail_form_fragment)
        }
        MainScreen.CameraPhoto -> {
            val navBuilder = NavOptions.Builder()
            val navOptions = navBuilder.setPopUpTo(R.id.camera_photo_fragment, true).build()
            findNavController().navigate(R.id.camera_photo_fragment, null, navOptions)
        }
        MainScreen.PhotoPreview -> {
            val navBuilder = NavOptions.Builder()
            val navOptions = navBuilder.setPopUpTo(R.id.photo_preview_fragment, true).build()
            findNavController().navigate(R.id.photo_preview_fragment, null, navOptions)
        }
        else -> {}
    }
}