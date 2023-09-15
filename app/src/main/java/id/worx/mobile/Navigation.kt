package id.worx.mobile

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.security.InvalidParameterException

enum class WelcomeScreen {
    Splash, Welcome, AdvancedSetting, CreateTeam, CreateTeamSubmitted,
    JoinTeam, WaitingVerification, VerificationRejected
}

enum class MainScreen {
    Home, Detail, CameraPhoto, PhotoPreview, SignaturePad, Settings, Licences, AdvanceSettings, Sketch, ScannerScreen, BarcodePreview
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

        WelcomeScreen.AdvancedSetting -> {
            findNavController().navigate(R.id.advanced_settings_fragment)
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
            if (from == MainScreen.Detail) {
                findNavController().navigate(R.id.action_detail_to_home)
            } else {
                findNavController().navigate(R.id.home_fragment)
            }
        }

        MainScreen.Detail -> {
            when (from) {
                MainScreen.SignaturePad ->
                    findNavController().navigate(R.id.action_signaturepad_to_detail)

                MainScreen.PhotoPreview ->
                    findNavController().navigate(R.id.action_previewfragment_to_detailfragment)

                MainScreen.ScannerScreen ->
                    findNavController().navigate(R.id.action_scannerFragment_to_detail_form_fragment)

                MainScreen.BarcodePreview -> {
                    findNavController().navigate(R.id.action_barcodePreviewFragment_to_detail_form_fragment)
                }

                MainScreen.Sketch ->
                    findNavController().navigate(R.id.action_sketchFragment_to_detail_form_fragment)

                else -> findNavController().navigate(R.id.detail_form_fragment)
            }
        }

        MainScreen.CameraPhoto -> {
            when (from) {
                MainScreen.PhotoPreview -> findNavController().navigate(R.id.action_photoPreviewFragment_to_cameraPhotoFragment)
                else -> findNavController().navigate(R.id.camera_photo_fragment)
            }
        }

        MainScreen.PhotoPreview -> {
            findNavController().navigate(R.id.action_photofragment_to_previewfragment)
        }

        MainScreen.SignaturePad -> {
            findNavController().navigate(R.id.signature_pad_fragment)
        }

        MainScreen.Settings -> {
            findNavController().navigate(R.id.settingsFragment)
        }

        MainScreen.Licences -> {
            findNavController().navigate(R.id.licencesFragment)
        }

        MainScreen.AdvanceSettings -> {
            findNavController().navigate(R.id.advanced_settings_fragment)
        }

        MainScreen.ScannerScreen -> {
            findNavController().navigate(R.id.scannerFragment)
        }

        MainScreen.BarcodePreview -> {
            findNavController().navigate(R.id.barcodePreviewFragment)
        }

        MainScreen.Sketch -> {
            findNavController().navigate(R.id.sketchFragment)
        }
    }
}