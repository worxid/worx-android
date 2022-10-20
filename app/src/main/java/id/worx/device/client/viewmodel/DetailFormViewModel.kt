package id.worx.device.client.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.Util
import id.worx.device.client.Util.getCurrentDate
import id.worx.device.client.Util.initProgress
import id.worx.device.client.WorxApplication
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.*
import id.worx.device.client.repository.SourceDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gotev.uploadservice.protocols.binary.BinaryUploadRequest
import java.io.File
import java.util.*
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
    private val application: WorxApplication,
    private val session: Session,
    private val savedStateHandle: SavedStateHandle,
    private val dataSourceRepo: SourceDataRepository
) : ViewModel() {

    var uiState = MutableStateFlow(DetailUiState())

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private val _formProgress = mutableStateOf(0)
    val formProgress: State<Int> = _formProgress

    /**
     * Pass data from Home ViewModel
     * Params : form - EmptyForm / SubmitForm
     */
    fun navigateFromHomeScreen(form: BasicForm) {
        uiState.update {
            if (form is SubmitForm){
                _formProgress.value = initProgress(form.values.toMutableMap(), form.fields)
                var status = EventStatus.Filling
                if (form.status == 2 || form.status == 1){
                    status = EventStatus.Done
                }
                it.copy(detailForm = form, values = form.values.toMutableMap(), status = status)
            } else {
                _formProgress.value = 0
                it.copy(detailForm = form, values= mutableMapOf(), status = EventStatus.Filling)
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

    /**
     * @param fileNames file path that needs to be uploaded
     * @param indexForm the index of fields component inside the form
     * @param typeValue 1 == File, 2 == Image
     */
    fun getPresignedUrl(fileNames: ArrayList<String>, indexForm:Int, typeValue: Int) {
        viewModelScope.launch {
            var value: Value = ImageValue(value = arrayListOf(), filePath = arrayListOf())
            if (typeValue == 1) {
                value = FileValue(value = arrayListOf(), filePath = arrayListOf())
            }

            fileNames.forEach {
                val file = File(it)
                val response = dataSourceRepo.getPresignedUrl(it)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        uploadMedia(response.body()!!.url!!, file)
                        if (typeValue == 1){
                            value as FileValue
                            value.filePath.add(response.body()!!.path!!)
                            value.value.add(response.body()!!.fileId!!)
                        } else {
                            value as ImageValue
                            value.filePath.add(response.body()!!.path!!)
                            value.value.add(response.body()!!.fileId!!)
                        }
                    } else {
                        Log.d("WORX", "file $it failed to get url")
                    }
                }
            }
            setComponentData(indexForm, value)
        }
    }

    private fun uploadMedia(url: String, myFile: File) {
        val request = BinaryUploadRequest(this.application, url)
            .setMethod("PUT")
            .setNotificationConfig { _: Context, uploadId: String ->
                Util.UploadNotificationConfig(this.application, uploadId, "File Upload")}
        request.setFileToUpload(myFile.path)
        request.startUpload()
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
            form.dbId?.let { dbId -> dataSourceRepo.deleteSubmitFormById(dbId) } // if it is draft delete from DB
            dataSourceRepo.insertOrUpdateSubmitForm(form) //insertSubmission to db
            _formProgress.value = 0
            uiState.update {
                it.copy(
                    detailForm = null,
                    values = mutableMapOf(),
                    currentComponent = -1,
                    status = EventStatus.Submitted
                )
            }
            _formProgress.value = 0
            actionAfterSubmitted()
            _navigateTo.value = Event(MainScreen.Home)
        }
    }

    fun saveFormAsDraft(actionAfterSaved: () -> Unit) {
        viewModelScope.launch {
            val form = createSubmitForm()
            dataSourceRepo.insertOrUpdateSubmitForm(form.copy(status = 0))
            actionAfterSaved()
            _navigateTo.value = Event(MainScreen.Home)
            uiState.update {
                it.copy(
                    detailForm = null,
                    values = mutableMapOf(),
                    currentComponent = -1,
                    status = EventStatus.Saved,
                )
            }
            _formProgress.value = 0
        }
    }

    private fun createSubmitForm(): SubmitForm{
        val submitForm = SubmitForm(
            id = uiState.value.detailForm!!.id,
            label = uiState.value.detailForm!!.label,
            description = uiState.value.detailForm!!.description,
            fields = uiState.value.detailForm!!.fields,
            values = uiState.value.values,
            templateId = uiState.value.detailForm!!.id,
            lastUpdated = getCurrentDate("dd/MM/yyyy hh:mm a"))

        val latitude = session.latitude ?: "0.0010"
        val longitude = session.longitude ?: "-109.3222"
        val geocoder = Geocoder(application.applicationContext, Locale.getDefault())
        val address = geocoder.getFromLocation(
            latitude.toDouble(),
            longitude.toDouble(),
            1
        )?.get(0) ?: Address(Locale.getDefault())
        val submitLocation = SubmitLocation(
            "${address.getAddressLine(0)}, ${address.subLocality}, " +
                    "${address.locality}, ${address.subAdminArea}, " +
                    "${address.adminArea}, ${address.countryName}",
            latitude.toDouble().toInt(),
            longitude.toDouble().toInt())
        submitForm.submitLocation = submitLocation
        return submitForm
    }
}