package id.worx.device.client.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import id.worx.device.client.data.DataStoreManager
import id.worx.device.client.data.DataStoreManager.Companion.SAVE_PHOTO_TO_GALLERY
import id.worx.device.client.screen.main.SettingScreen
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel by activityViewModels<HomeViewModel>()
    private val detailViewModel by activityViewModels<DetailFormViewModel>()

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private var savePhoto = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CoroutineScope(Dispatchers.Main).launch {
            savePhoto = dataStoreManager.readBool(SAVE_PHOTO_TO_GALLERY) ?: false
        }

        return ComposeView(requireActivity()).apply {
            setContent {
                WorxTheme() {
                    SettingScreen(viewModel, onBackNavigation = { activity?.onBackPressedDispatcher?.onBackPressed() } , savePhoto)
                }
            }
        }
    }

}