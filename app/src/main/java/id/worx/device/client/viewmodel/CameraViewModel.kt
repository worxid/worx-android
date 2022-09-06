package id.worx.device.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.repository.HomeRepository
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private var _photoPath = MutableLiveData<String?>()
    val photoPath: LiveData<String?> = _photoPath

    init {
        _photoPath.value = null
    }

    fun goToPreviewPhoto(photoPath: String) {
        _photoPath.value = photoPath
        _navigateTo.value = Event(MainScreen.PhotoPreview)
    }

    fun rejectPhoto(){
        _photoPath.value = null
        _navigateTo.value = Event(MainScreen.CameraPhoto)
    }

    fun acceptPhoto() {
        _photoPath.value = null
        _navigateTo.value = Event(MainScreen.Detail)
    }

}