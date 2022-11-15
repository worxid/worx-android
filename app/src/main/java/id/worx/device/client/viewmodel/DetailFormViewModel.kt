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
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

enum class EventStatus {
    Loading, Filling, Submitted, Done, Saved, Edited
}

/**
 * UI State to hold data
 */
data class DetailUiState(
    val detailForm: BasicForm? = null,
    val values: MutableMap<String, Value> = mutableMapOf(),
    val currentComponent: Int = -1,
    val status: EventStatus = EventStatus.Loading,
    val errorMessages: String = "",
)

@HiltViewModel
class DetailFormViewModel @Inject constructor(
    private val application: WorxApplication,
    private val session: Session,
    private val savedStateHandle: SavedStateHandle,
    private val dataSourceRepo: SourceDataRepository
) : ViewModel() {

    var uiState = MutableStateFlow(DetailUiState())
    lateinit var uiHandler: UIHandler

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private val _formProgress = mutableStateOf(0)
    val formProgress: State<Int> = _formProgress

    val indexScroll = mutableStateOf(0)

    val offset = mutableStateOf(0)

    /**
     * Pass data from Home ViewModel
     * Params : form - EmptyForm / SubmitForm
     */
    fun navigateFromHomeScreen(form: BasicForm) {
        uiState.update {
            if (form is SubmitForm) {
                _formProgress.value = initProgress(form.values.toMutableMap(), form.fields)
                var status = EventStatus.Filling
                if (form.status == 2 || form.status == 1) {
                    status = EventStatus.Done
                }
                it.copy(detailForm = form, values = form.values.toMutableMap(), status = status)
            } else {
                _formProgress.value = 0
                it.copy(detailForm = form, values = mutableMapOf(), status = EventStatus.Filling)
            }
        }
    }

    /**
     * Update value on the UI State
     */
    fun setComponentData(index: Int, value: Value?) {
        val fields = uiState.value.detailForm!!.fields
        val id = fields[index].id!!
        val values = uiState.value.values.toMutableMap()
        val progressBit = 100 / fields.size

        uiState.update {
            if (value == null) {
                _formProgress.value -= progressBit
                values.remove(id)
                it.copy(values = values)
            } else {
                if (it.values.contains(fields[index].id)) {
                    values.replace(id, value)
                    it.copy(values = values)
                } else {
                    _formProgress.value += progressBit
                    values[id] = value
                    it.copy(values = values)
                }
            }
        }
    }

    fun goToCameraPhoto(index: Int) {
        uiState.update {
            it.copy(currentComponent = index)
        }
        _navigateTo.value = Event(MainScreen.CameraPhoto)
    }

    fun goToSignaturePad(index: Int) {
        uiState.update {
            it.copy(currentComponent = index)
        }
        _navigateTo.value = Event(MainScreen.SignaturePad)
    }

    fun saveSignature(bitmap: Bitmap) {
        val fileName =
            "signature${uiState.value.detailForm!!.fields[uiState.value.currentComponent].id}"
        val filePath = createFileFromBitmap(fileName, bitmap)
        getPresignedUrlForSignature(uiState.value.currentComponent, bitmap, filePath)
        _navigateTo.value = Event(MainScreen.Detail)
    }

    private fun createFileFromBitmap(fileName: String, bitmp: Bitmap): String {
        val file = File(this.application.cacheDir, "$fileName.png")
        file.createNewFile()
        val fos = FileOutputStream(file)
        bitmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        return file.absolutePath
    }

    fun currentComponentIndex(index: Int) {
        uiState.update {
            it.copy(currentComponent = index)
        }
    }

    /**
     * @param fileNames file path that needs to be uploaded
     * @param indexForm the index of fields component inside the form
     * @param typeValue 1 == File, 2 == Image
     */
    fun getPresignedUrl(fileNames: ArrayList<String>, indexForm: Int, typeValue: Int) {
        val value: Value = if (typeValue == 2){
            uiState.value.values[uiState.value.detailForm!!.fields[indexForm].id] ?: ImageValue()
        } else {
            uiState.value.values[uiState.value.detailForm!!.fields[indexForm].id] ?: FileValue()
        }

        val valuesToRemove = mutableSetOf<String>()
        fileNames.forEach {
            if ((value is FileValue && value.filePath.contains(it)) ||
                (value is ImageValue && value.filePath.contains(it))
            ) {
                valuesToRemove.add(it)
            }
        }
        fileNames.removeAll(valuesToRemove)

        viewModelScope.launch {
            fileNames.forEach {
                val file = File(it)
                val response = dataSourceRepo.getPresignedUrl(it)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        if (typeValue == 1 && value is FileValue) {
                            uploadMedia(response.body()!!.url!!, file, "File $it")
                            value.filePath.add(it)
                            value.value.add(response.body()!!.fileId!!)
                        } else if (typeValue == 2 && value is ImageValue) {
                            uploadMedia(response.body()!!.url!!, file, "Image $it")
                            value.filePath.add(it)
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

    private fun getPresignedUrlForSignature(indexForm: Int, bitmap: Bitmap, fileName: String) {
        viewModelScope.launch {
            val file = File(fileName)
            val response = dataSourceRepo.getPresignedUrl(fileName)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    uploadMedia(response.body()!!.url!!, file, "Signature $fileName")
                    setComponentData(
                        indexForm,
                        SignatureValue(value = response.body()!!.fileId, bitmap = bitmap)
                    )
                } else {
                    Log.d("WORX", "signature $fileName failed to get url")
                }
            }
        }
    }

    private fun uploadMedia(url: String, myFile: File, fileExplaination: String) {
        val request = BinaryUploadRequest(this.application, url)
            .setMethod("PUT")
            .setNotificationConfig { _: Context, uploadId: String ->
                Util.UploadNotificationConfig(
                    this.application,
                    uploadId,
                    "File Upload",
                    fileExplaination
                )
            }
        request.setFileToUpload(myFile.path)
        request.startUpload()
    }

    private fun submitForm(actionAfterSubmitted: () -> Unit) {
        viewModelScope.launch {
            val form = createSubmitForm()
            if (Util.isConnected(application.applicationContext)) {
                val result = dataSourceRepo.submitForm(form)
                if (result.isSuccessful) {
                    dataSourceRepo.insertOrUpdateSubmitForm(form.copy(status = 2)) //insertSubmission to db
                } else {
                    dataSourceRepo.insertOrUpdateSubmitForm(form.copy(status = 1)) //insertSubmission to db
                }
            } else {
                dataSourceRepo.insertOrUpdateSubmitForm(form.copy(status = 1)) //insertSubmission to db
            }
            form.dbId?.let { dbId -> dataSourceRepo.deleteSubmitFormById(dbId) } // if it is draft delete from DB
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

    fun validateSubmitForm(actionAfterSubmitted: () -> Unit) {
        val fields = uiState.value.detailForm!!.fields
        val value = uiState.value.values
        val unFilledFields = arrayListOf<String>()

        fields.forEach { it ->
            var totalCheck = 0
            var minCheck = 0
            if (it.type == Type.Checkbox.name){
               val checkBoxValue = value[it.id] as CheckBoxValue
               minCheck = (it as CheckBoxField).minChecked ?: 0
               totalCheck = checkBoxValue.value.filter { it }.size
            }

            if (totalCheck in 1 until minCheck) {
                unFilledFields.add("${it.type} ${it.label}")
            } else if (it.required == true && value[it.id] == null) {
                unFilledFields.add("${it.type} ${it.label}")
            }
        }

        if (unFilledFields.isEmpty()) {
            submitForm { actionAfterSubmitted() }
        } else {
            uiHandler.showToast("Form is not complete!")
            uiState.update { it.copy(status = EventStatus.Filling,)
            }
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

    private fun createSubmitForm(): SubmitForm {
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
            longitude.toDouble().toInt()
        )

        return SubmitForm(
            id = uiState.value.detailForm!!.id,
            label = uiState.value.detailForm!!.label,
            description = uiState.value.detailForm!!.description,
            fields = uiState.value.detailForm!!.fields,
            values = uiState.value.values,
            templateId = uiState.value.detailForm!!.id,
            lastUpdated = getCurrentDate("dd/MM/yyyy hh:mm a"),
            submitLocation = submitLocation
        )
    }

    interface UIHandler {
        fun showToast(text: String)
    }
}