package id.worx.mobile.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import id.worx.mobile.screen.main.PhotoPreviewScreen
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.CameraViewModel
import id.worx.mobile.viewmodel.DetailFormViewModel
import id.worx.mobile.viewmodel.ThemeViewModelImpl
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class PhotoPreviewFragment: Fragment() {

    private val viewModel by activityViewModels<CameraViewModel>()
    private val detailViewModel by activityViewModels<DetailFormViewModel>()
    private val themeViewModel by activityViewModels<ThemeViewModelImpl>()

    @Inject
    lateinit var session: Session
    private var saveToGallery = false

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
                navigate(navigateTo, MainScreen.PhotoPreview)
            }
        }

        saveToGallery = session.isSaveImageToGallery

        return ComposeView(requireContext()).apply {
            setContent {
                val theme = themeViewModel.theme.value
                WorxTheme(theme = theme) {
                    WorxThemeStatusBar()
                    PhotoPreviewScreen(viewModel, detailViewModel) { path: String ->
                        galleryAddPic(
                            saveToGallery,
                            path,
                            requireContext()
                        )
                    }
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

    private fun galleryAddPic(saveToGallery : Boolean, mCurrentPhotoPath: String, context: Context) {
        if (saveToGallery) {
            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                val f = File(mCurrentPhotoPath)
                mediaScanIntent.data = Uri.fromFile(f)
                context.sendBroadcast(mediaScanIntent)
            }
        }
    }
}