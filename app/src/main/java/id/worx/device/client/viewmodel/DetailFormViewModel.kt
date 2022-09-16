package id.worx.device.client.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.SignatureValue
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.model.Value
import id.worx.device.client.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EventStatus {
    Loading, Filling, Submitted, Done, Saved
}

/**
 * UI State to hold data
 */
data class DetailUiState(
    var detailForm: EmptyForm? = null,
    var values: MutableMap<String, Value> = mutableMapOf(),
    var currentComponent: Int = -1,
    val status: EventStatus = EventStatus.Loading,
    var errorMessages: List<String> = emptyList(),
)

@HiltViewModel
class DetailFormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: HomeRepository
) : ViewModel() {

    var uiState = MutableStateFlow(DetailUiState())

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private val _formProgress = mutableStateOf(0)
    val formProgress: State<Int> = _formProgress

    fun navigateFromHomeScreen(form: EmptyForm) {
        uiState.update {
            it.copy(detailForm = form, status = EventStatus.Filling)
        }
    }

    fun setComponentData(index: Int, value: Value?) {
        val fields = uiState.value.detailForm!!.fields
        val values = uiState.value.values
        val progressBit = 100 / fields.size

        uiState.update {
            if (value == null) {
                _formProgress.value -= progressBit
                values.remove(fields[index].id)
                it.copy(values = values)
            } else {
                if (it.values.contains(fields[index].id)) {
                    values.replace(fields[index].id!!, value)
                    it.copy(values = values)
                } else {
                    _formProgress.value += progressBit
                    values[fields[index].id!!] = value
                    it.copy(values = values)
                }
            }
        }
    }

    fun goToCameraPhoto(index: Int) {
        uiState.value.currentComponent = index
        _navigateTo.value = Event(MainScreen.CameraPhoto)
    }

    fun goToSignaturePad(index: Int) {
        uiState.value.currentComponent = index
        _navigateTo.value = Event(MainScreen.SignaturePad)
    }

    fun saveSignature(bitmap: Bitmap?){
        setComponentData(uiState.value.currentComponent, SignatureValue(value = 1, bitmap = bitmap))
        _navigateTo.value = Event(MainScreen.Detail)
    }

    fun currentComponentIndex(index: Int) {
        uiState.value.currentComponent = index
    }

    fun submitForm() {
        viewModelScope.launch {
            val result = repository.submitForm(
                SubmitForm(
                    id = uiState.value.detailForm!!.id,
                    label = uiState.value.detailForm!!.label,
                    description = uiState.value.detailForm!!.description,
                    fields = uiState.value.detailForm!!.fields,
                    values = uiState.value.values
                )
            )
            if (result.isSuccessful){
                uiState.update {
                    it.copy(status = EventStatus.Submitted)
                }
            }
        }
    }

    fun saveFormAsDraft() {
        uiState.update {
            it.copy(status = EventStatus.Saved)
        }
    }
}