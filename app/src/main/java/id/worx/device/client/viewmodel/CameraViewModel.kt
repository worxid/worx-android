package id.worx.device.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
) : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private var _photoPath = MutableLiveData<String?>()
    val photoPath: LiveData<String?> = _photoPath

    private var _indexForm = MutableLiveData<Int?>()
    val indexForm: LiveData<Int?> = _indexForm

    init {
        _photoPath.value = null
    }

    fun navigateFromDetailScreen(indexForm : Int){
        _indexForm.value = indexForm
    }

    fun goToPreviewPhoto(photoPath: String) {
        _photoPath.value = photoPath
        _navigateTo.value = Event(MainScreen.PhotoPreview)
    }

    fun rejectPhoto(){
        _photoPath.value = null
        _navigateTo.value = Event(MainScreen.CameraPhoto)
    }

    fun navigateToDetail() {
        _photoPath.value = null
        _indexForm.value = null
        _navigateTo.value = Event(MainScreen.Detail)
    }

}