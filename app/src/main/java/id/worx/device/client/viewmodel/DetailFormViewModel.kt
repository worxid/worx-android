package id.worx.device.client.viewmodel

import androidx.compose.runtime.*
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

enum class EventStatus{
    Loading, Filling, Submitted, Done, Saved
}

/**
 * UI State to hold data
 */
data class DetailUiState(
    var detailForm: Form? = null,
    var currentComponent: Int = -1,
    val status: MutableState<EventStatus> = mutableStateOf(EventStatus.Loading),
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

    private val _formProgress = mutableStateOf(0)
    val formProgress: State<Int> = _formProgress

    /**
     * Notify that an error was displayed on the screen
     */
    fun errorShown(errorId: Long) {
        uiState.errorMessages = listOf("Error $errorId")
    }

    fun navigateFromHomeScreen(form: Form){
        uiState.detailForm = form
        uiState.status.value = EventStatus.Filling
    }

    fun setComponentData(index:Int, data: String?){
        val componentList = uiState.detailForm?.componentList
        componentList?.get(index)?.Outputdata = data
        uiState.detailForm = uiState.detailForm?.copy(
            componentList = componentList!!
        )

        val progressBit = 100/componentList!!.size
        if (!data.isNullOrEmpty()){
            _formProgress.value += progressBit
        } else {
            _formProgress.value -= progressBit
        }
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
        setComponentData(uiState.currentComponent, bitmap)
        _navigateTo.value = Event(MainScreen.Detail)
    }

    fun currentComponentIndex(index: Int){
        uiState.currentComponent = index
    }

    fun submitForm(){
        uiState.status.value = EventStatus.Submitted
    }

    fun saveFormAsDraft(){
        uiState.status.value = EventStatus.Saved
    }
}