package id.worx.device.client.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.model.Form
import id.worx.device.client.repository.HomeRepository
import javax.inject.Inject


/**
 * UI State to hold data
 */
data class DetailUiState(
    var detailForm: Form? = null,
    var currentComponent: Int = -1,
    val isLoading: Boolean = false,
    var errorMessages: List<String> = emptyList(),
)

@HiltViewModel
class DetailFormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: HomeRepository
) : ViewModel() {

    var uiState by mutableStateOf(DetailUiState())
        private set

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    /**
     * Notify that an error was displayed on the screen
     */
    fun errorShown(errorId: Long) {
        uiState.errorMessages = listOf("Error $errorId")
    }

    fun navigateFromHomeScreen(form: Form){
        uiState.detailForm = form
    }

    fun setComponentData(index:Int, data: String?){
        val componentList = uiState.detailForm?.componentList
        componentList?.get(index)?.Outputdata = data
        uiState.detailForm = uiState.detailForm?.copy(
            componentList = componentList!!
        )
    }

    fun goToCameraPhoto(index: Int) {
        uiState.currentComponent = index
        _navigateTo.value = Event(MainScreen.CameraPhoto)
    }

    fun goToSignaturePad(index: Int) {
        uiState.currentComponent = index
        _navigateTo.value = Event(MainScreen.SignaturePad)
    }

    fun saveSignature(bitmap: String){
        val componentList = uiState.detailForm?.componentList
        componentList?.get(uiState.currentComponent)?.Outputdata = bitmap
        uiState.detailForm = uiState.detailForm?.copy(
            componentList = componentList!!
        )
        _navigateTo.value = Event(MainScreen.Detail)
    }

    fun currentComponentIndex(index: Int){
        uiState.currentComponent = index
    }
}