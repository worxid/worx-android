package id.worx.mobile.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.mobile.Event
import id.worx.mobile.MainScreen
import id.worx.mobile.Util
import id.worx.mobile.Util.getCurrentDate
import id.worx.mobile.Util.initProgress
import id.worx.mobile.WorxApplication
import id.worx.mobile.data.api.SyncServer
import id.worx.mobile.data.database.Session
import id.worx.mobile.domain.GetSubmitLocationUseCase
import id.worx.mobile.domain.request.GetSubmitLocationRequest
import id.worx.mobile.model.BasicForm
import id.worx.mobile.model.SubmitForm
import id.worx.mobile.model.SubmitLocation
import id.worx.mobile.model.Type
import id.worx.mobile.model.Value
import id.worx.mobile.model.fieldmodel.CheckBoxField
import id.worx.mobile.model.fieldmodel.CheckBoxValue
import id.worx.mobile.model.fieldmodel.FileValue
import id.worx.mobile.model.fieldmodel.ImageValue
import id.worx.mobile.model.fieldmodel.SignatureValue
import id.worx.mobile.model.fieldmodel.SketchValue
import id.worx.mobile.repository.SourceDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gotev.uploadservice.protocols.binary.BinaryUploadRequest
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.collections.set

enum class EventStatus {
    Loading, Filling, Submitted, Done, Saved
}

/**
 * UI State to hold data
 */
data class DetailUiState(
    val detailForm: BasicForm? = null,
    val values: Map<String, Value> = mutableStateMapOf(),
    val currentComponent: Int = -1,
    val status: EventStatus = EventStatus.Loading,
    val errorMessages: String = "",
)

@HiltViewModel
class DetailFormViewModel @Inject constructor(
    private val application: WorxApplication,
    private val session: Session,
    private val dataSourceRepo: SourceDataRepository,
    private val syncServerWork: SyncServer,
    private val getSubmitLocationUseCase: GetSubmitLocationUseCase
) : ViewModel() {

    var uiState = MutableStateFlow(DetailUiState())

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> = _toastMessage

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private val _formProgress = mutableStateOf(0)
    val formProgress: State<Int> = _formProgress

    val indexScroll = mutableStateOf(0)

    val offset = mutableStateOf(0)

    private val _navigateFrom = MutableLiveData<MainScreen>()
    val navigateFrom = _navigateFrom

    private val _cameraResultUri = MutableLiveData<Uri?>()
    val cameraResultUri = _cameraResultUri

    /**
     * Pass data from Home ViewModel
     * Params : form - EmptyForm / SubmitForm
     */
    fun navigateFromHomeScreen(form: BasicForm) {
        uiState.update {
            when (form) {
                is SubmitForm -> {
                    _formProgress.value = initProgress(form.values.toMutableMap(), form.fields)
                    var status = EventStatus.Filling
                    if (form.status == 2 || form.status == 1) {
                        status = EventStatus.Done
                    }
                    it.copy(detailForm = form, values = form.values.toMutableMap(), status = status)
                }

                else -> {
                    _formProgress.value = 0
                    it.copy(
                        detailForm = form,
                        values = mutableMapOf(),
                        status = EventStatus.Filling
                    )
                }
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
        val separatorCount = fields.count { it.type == Type.Separator.type }
        val progressBit = 100 / (fields.size - separatorCount)

        uiState.update {
            if (value == null) {
                _formProgress.value -= progressBit
                values.remove(id)
                it.copy(values = values.toMap())
            } else {
                if (it.values.contains(fields[index].id)) {
                    values.replace(id, value)
                    it.copy(values = values.toMap())
                } else {
                    _formProgress.value += progressBit
                    values[id] = value
                    it.copy(values = values.toMap())
                }
            }
        }
    }

    fun goToCameraPhoto(index: Int? = null, navigateFrom: MainScreen) {
        if (index != null) {
            uiState.update {
                it.copy(currentComponent = index)
            }
        }
        _navigateTo.value = Event(MainScreen.CameraPhoto)
        _navigateFrom.value = navigateFrom
    }

    fun goToScannerBarcode(index: Int) {
        uiState.update {
            it.copy(currentComponent = index)
        }
        _navigateTo.value = Event(MainScreen.ScannerScreen)
    }

    fun goToSignaturePad(index: Int) {
        uiState.update {
            it.copy(currentComponent = index)
        }
        _navigateTo.value = Event(MainScreen.SignaturePad)
    }

    fun goToSketch(index: Int) {
        uiState.update {
            it.copy(currentComponent = index)
        }
        _navigateTo.value = Event(MainScreen.Sketch)
    }

    fun saveSignature(bitmap: Bitmap, index: Int) {
        val fileName =
            "signature${uiState.value.detailForm!!.fields[index].id}"
        val filePath = createFileFromBitmap(fileName, bitmap)
        getPresignedUrlForSignature(index, bitmap, filePath)
        _navigateTo.value = Event(MainScreen.Detail)
    }

    fun saveSketch(bitmap: Bitmap, index: Int) {
        val fileName = "sketch${uiState.value.detailForm!!.fields[index].id}"
        val filePath = createFileFromBitmap(fileName, bitmap)
        getPresignedUrlForSketch(index, bitmap, filePath)
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

    fun setCameraResultUri(uri: Uri) {
        _cameraResultUri.value = null
        _cameraResultUri.value = uri
    }

    fun goToHome() {
        _navigateTo.value = Event(MainScreen.Home)
    }

    /**
     * @param fileNames file path that needs to be uploaded
     * @param indexForm the index of fields component inside the form
     * @param typeValue 1 == File, 2 == Image
     */
    fun getPresignedUrl(fileNames: ArrayList<String>, indexForm: Int, typeValue: Int) {
        val value: Value = if (typeValue == 2) {
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

    private fun getPresignedUrlForSketch(indexForm: Int, bitmap: Bitmap, fileName: String) {
        viewModelScope.launch {
            val file = File(fileName)
            val response = dataSourceRepo.getPresignedUrl(fileName)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    uploadMedia(response.body()!!.url!!, file, "Sketch $fileName")
                    setComponentData(
                        indexForm,
                        SketchValue(value = response.body()!!.fileId, bitmap = bitmap)
                    )
                } else {
                    Log.e("WORX", "sketch $fileName failed to get url")
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
            val submitLocation = withContext(Dispatchers.Default) {
                return@withContext async {
                    getSubmitLocationUseCase.execute(
                        GetSubmitLocationRequest(
                            application.applicationContext,
                            session
                        ),
                    )
                }.await()
            }
            val form = createSubmitForm(submitLocation)
            if (Util.isNetworkAvailable(application.applicationContext)) {
                val result = dataSourceRepo.submitForm(form)
                if (result.isSuccessful) {
                    dataSourceRepo.insertOrUpdateSubmitForm(form.copy(status = 2)) //insertSubmission to db
                } else {
                    _toastMessage.value = Event("Submit Form Error ${result.code()}")
                }
            } else {
                dataSourceRepo.insertOrUpdateSubmitForm(form.copy(status = 1)) //insertSubmission to db
            }
            if (uiState.value.detailForm is SubmitForm) {
                (uiState.value.detailForm as SubmitForm).dbId?.let { dbId ->
                    dataSourceRepo.deleteSubmitFormById(
                        dbId
                    )
                } // if it is draft delete from DB
            }
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
            if (it.type == Type.Checkbox.name) {
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
            _toastMessage.value = Event("Form is not complete!")
            uiState.update {
                it.copy(status = EventStatus.Filling)
            }
        }
    }

    fun saveFormAsDraft(draftDescription: String, actionAfterSaved: () -> Unit) {
        viewModelScope.launch {
            if (uiState.value.detailForm is SubmitForm) {
                (uiState.value.detailForm as SubmitForm).dbId?.let { dbId ->
                    dataSourceRepo.deleteSubmitFormById(
                        dbId
                    )
                }
            }
            val submitLocation = withContext(Dispatchers.Default) {
                return@withContext async {
                    getSubmitLocationUseCase.execute(
                        GetSubmitLocationRequest(
                            application.applicationContext,
                            session
                        )
                    )
                }.await()
            }
            val form = createSubmitForm(
                submitLocation = submitLocation,
                draftDescription = draftDescription
            )
            dataSourceRepo.insertOrUpdateSubmitForm(form.copy(status = 0))
            actionAfterSaved()
            _formProgress.value = 0

            uiState.update {
                it.copy(
                    detailForm = null,
                    values = mutableMapOf(),
                    currentComponent = -1,
                    status = EventStatus.Saved,
                )
            }
            _formProgress.value = 0
            _navigateTo.value = Event(MainScreen.Home)
        }
    }

    fun deleteDraft(draftForm: SubmitForm, isFromDetailPage: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            draftForm.dbId?.let {
                dataSourceRepo.deleteSubmitFormById(it)
            }
            if (isFromDetailPage) {
                _navigateTo.value = Event(MainScreen.Home)
            }
        }
    }

    fun duplicateDraft(draftForm: SubmitForm, draftDescription: String) {
        viewModelScope.launch {
            val submitLocation = withContext(Dispatchers.Default) {
                return@withContext async {
                    getSubmitLocationUseCase.execute(
                        GetSubmitLocationRequest(
                            application.applicationContext,
                            session
                        )
                    )
                }.await()
            }
            val newDraftForm = createDraftForm(draftForm, submitLocation, draftDescription)
            dataSourceRepo.insertOrUpdateSubmitForm(newDraftForm)
            _formProgress.value = 0

            uiState.update {
                it.copy(
                    detailForm = null,
                    values = mutableMapOf(),
                    currentComponent = -1,
                    status = EventStatus.Saved,
                )
            }
            _formProgress.value = 0
            _navigateTo.value = Event(MainScreen.Home)
        }
    }

    private fun createDraftForm(
        draftForm: SubmitForm,
        submitLocation: SubmitLocation,
        draftDescription: String = ""
    ): SubmitForm {
        return SubmitForm(
            id = draftForm.id,
            label = draftForm.label,
            description = draftDescription.ifBlank { draftForm.description },
            fields = draftForm.fields,
            values = draftForm.values,
            templateId = draftForm.id,
            lastUpdated = getCurrentDate("dd/MM/yyyy hh:mm a"),
            submitLocation = submitLocation,
            status = 0
        )
    }

    private fun createSubmitForm(
        submitLocation: SubmitLocation,
        draftDescription: String = ""
    ): SubmitForm {
        return SubmitForm(
            id = uiState.value.detailForm!!.id,
            label = uiState.value.detailForm!!.label,
            description = draftDescription.ifBlank { uiState.value.detailForm!!.description },
            fields = uiState.value.detailForm!!.fields,
            values = uiState.value.values,
            templateId = uiState.value.detailForm!!.id,
            lastUpdated = getCurrentDate("dd/MM/yyyy hh:mm a"),
            submitLocation = submitLocation
        )
    }

    fun refreshData() {
        viewModelScope.launch {
            val submitForm =
                uiState.value.detailForm?.id?.let { dataSourceRepo.getSubmissionById(it) }
            val form = uiState.value.detailForm?.id?.let { dataSourceRepo.getEmptyFormByID(it) }
            if (submitForm == null) {
                uiState.update {
                    it.copy(detailForm = form)
                }
            } else {
                uiState.update {
                    it.copy(detailForm = form, values = submitForm.values.toMutableMap())
                }
            }
        }
    }


    fun syncWithServer(typeData: Int) {
        viewModelScope.launch {
            syncServerWork.syncWithServer(typeData)
        }
    }
}