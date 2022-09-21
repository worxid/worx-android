package id.worx.device.client.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.Util
import id.worx.device.client.WorxApplication
import id.worx.device.client.model.*
import id.worx.device.client.repository.SourceDataRepository
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
    var detailForm: BasicForm? = null,
    var values: MutableMap<String, Value> = mutableMapOf(),
    var currentComponent: Int = -1,
    val status: EventStatus = EventStatus.Loading,
    var errorMessages: String = "",
)

@HiltViewModel
class DetailFormViewModel @Inject constructor(
    val application: WorxApplication,
    private val savedStateHandle: SavedStateHandle,
    private val dataSourceRepo: SourceDataRepository
) : ViewModel() {

    var uiState = MutableStateFlow(DetailUiState())

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private val _formProgress = mutableStateOf(0)
    val formProgress: State<Int> = _formProgress

    /**
     * If the form is half filled, set the progress value
     */
    private fun initProgress(values: MutableMap<String, Value>, fields: ArrayList<Fields>){
        val progressBit = 100 / fields.size
        if (values.isNotEmpty()) {
            _formProgress.value = progressBit * values.size
        }
    }


    /**
     * Pass data from Home ViewModel
     * Params : form - EmptyForm / SubmitForm
     */
    fun navigateFromHomeScreen(form: BasicForm) {
        uiState.update {
            if (form is SubmitForm){
                viewModelScope.launch {
                    form.dbId?.let { dbId -> dataSourceRepo.deleteSubmitFormById(dbId) }
                }
                initProgress(form.values.toMutableMap(), form.fields)
                it.copy(detailForm = form, values = form.values.toMutableMap(), status = EventStatus.Filling)
            } else {
                it.copy(detailForm = form, status = EventStatus.Filling)
            }
        }
    }

    /**
     * Update value on the UI State
     */
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

    fun submitForm(actionAfterSubmitted: () -> Unit) {
        viewModelScope.launch {
            val form = createSubmitForm()

            if (Util.isConnected(application.applicationContext)) {
                val result = dataSourceRepo.submitForm(form)
                if (result.isSuccessful) {
                    form.status = 2
                } else {
                    form.status = 1
                }
            } else {
                form.status = 1
            }
            dataSourceRepo.insertOrUpdateSubmitForm(form) //insertSubmission to db
            uiState.update {
                it.copy(
                    detailForm = null,
                    values = mutableMapOf(),
                    currentComponent = -1,
                    status = EventStatus.Submitted
                )
            }
            actionAfterSubmitted()
            _navigateTo.value = Event(MainScreen.Home)
        }
    }

    fun saveFormAsDraft() {
        viewModelScope.launch {
            val form = createSubmitForm()
            dataSourceRepo.insertOrUpdateSubmitForm(form.copy(status = 0))
            uiState.update {
                it.copy(
                    detailForm = null,
                    values = mutableMapOf(),
                    currentComponent = -1,
                    status = EventStatus.Saved
                )
            }
            _navigateTo.value = Event(MainScreen.Home)
        }
    }

    private fun createSubmitForm(): SubmitForm{
        return SubmitForm(
            id = uiState.value.detailForm!!.id,
            label = uiState.value.detailForm!!.label,
            description = uiState.value.detailForm!!.description,
            fields = uiState.value.detailForm!!.fields,
            values = uiState.value.values,
            templateId = uiState.value.detailForm!!.id
        )
    }
}