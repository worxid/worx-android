package id.worx.mobile.viewmodel

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.mobile.BuildConfig
import id.worx.mobile.Event
import id.worx.mobile.MainScreen
import id.worx.mobile.data.api.SyncServer
import id.worx.mobile.data.database.Session
import id.worx.mobile.model.DeviceInfo
import id.worx.mobile.model.EmptyForm
import id.worx.mobile.model.FormSortModel
import id.worx.mobile.model.ResponseDeviceInfo
import id.worx.mobile.model.SubmitForm
import id.worx.mobile.repository.DeviceInfoRepository
import id.worx.mobile.repository.SourceDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gotev.uploadservice.extensions.isValidHttpUrl
import javax.inject.Inject

// Const to view dialog/notification on the screen
private val SUBMITTED = 1
private val DRAFT = 2

data class HomeUiState(
    var list: List<EmptyForm> = emptyList(),
    var drafts: List<SubmitForm> = emptyList(),
    var submission: List<SubmitForm> = emptyList(),
    var isLoading: Boolean = false,
    var errorMessages: String = "",
    var searchInput: String = "",
    val selectedSort: FormSortModel = FormSortModel()
)

sealed interface HomeUiEvent {
    data class OnSortClicked(val sortModel: FormSortModel) : HomeUiEvent
}

abstract class HomeViewModel : ViewModel() {
    abstract fun goToDetailScreen()
    abstract fun goToSettingScreen()
    abstract fun goToLicencesScreen()
    abstract fun goToAdvanceSettings()
    abstract fun onSearchInputChanged(searchInput: String)
    abstract fun showNotification(typeOfNotification: Int)
    abstract fun showBadge(typeOfBadge: Int)
    abstract fun syncWithServer(typeData: Int)
    abstract fun leaveTeam(
        onSuccess: () -> Unit,
        onError: () -> Unit,
        deviceCode: String
    )

    abstract fun getDeviceInfo(session: Session)
    abstract fun updateDeviceInfo(session: Session)
    abstract fun saveServerUrl(session: Session, url: String)
}

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val repository: SourceDataRepository,
    private val syncServerWork: SyncServer
) : HomeViewModel() {

    val uiState = MutableStateFlow(HomeUiState())
    lateinit var uiHandler: UIHandler

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private val _showNotification = MutableStateFlow(0)
    val showNotification: StateFlow<Int> = _showNotification

    private val _showBadge = MutableStateFlow(0)
    val showBadge: StateFlow<Int> = _showBadge

    init {
        updateData()
    }

    override fun goToDetailScreen() {
        _navigateTo.value = Event(MainScreen.Detail)
    }

    override fun goToSettingScreen() {
        _navigateTo.value = Event(MainScreen.Settings)
    }

    override fun goToLicencesScreen() {
        _navigateTo.value = Event(MainScreen.Licences)
    }

    override fun goToAdvanceSettings() {
        _navigateTo.value = Event(MainScreen.AdvanceSettings)
    }

    fun goToScannerScreen() {
        _navigateTo.value = Event((MainScreen.ScannerScreen))
    }

    fun onEvent(homeUiEvent: HomeUiEvent) {
        when (homeUiEvent) {
            is HomeUiEvent.OnSortClicked -> onSortUpdated(homeUiEvent.sortModel)
        }
    }

    private fun onSortUpdated(formSortModel: FormSortModel) {
        uiState.update {
            it.copy(selectedSort = formSortModel)
        }
        syncWithServer(SyncServer.DOWNLOADFROMSERVER)
    }

    /**
     * Refresh data and update the UI state accordingly
     */
    fun updateData() {
        uiState.update {
            it.copy(isLoading = true)
        }
        val selectedSort = uiState.value.selectedSort

        viewModelScope.launch(Dispatchers.IO) {
            val getAllFormFromDB = repository.getAllFormFromDB(formSortModel = selectedSort)
            val getAllDraft = repository.getAllDraftForm(formSortModel = selectedSort)
            val getAllSubmission = repository.getAllSubmission(formSortModel = selectedSort)

            uiState.update {
                it.copy(
                    list = getAllFormFromDB,
                    drafts = getAllDraft,
                    submission = getAllSubmission,
                    isLoading = false,
                    errorMessages = ""
                )
            }
        }
    }

    /**
     * Notify that the user updated the search query
     */
    override fun onSearchInputChanged(searchInput: String) {
        uiState.value.searchInput = searchInput
    }

    /**
     * Show dialog of notification on the screen
     * Params : typeOfNotification that need to be shown
     */
    override fun showNotification(typeOfNotification: Int) {
        _showNotification.value = typeOfNotification
    }

    /**
     * Show badge if there is new draft or submission saved
     * Params : string resources R.String.draft or R.string.submission
     */
    override fun showBadge(typeOfBadge: Int) {
        _showBadge.value = typeOfBadge
    }

    override fun syncWithServer(typeData: Int) {
        viewModelScope.launch {
            syncServerWork.syncWithServer(typeData)
        }
    }

    override fun leaveTeam(
        onSuccess: () -> Unit,
        onError: () -> Unit,
        deviceCode: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = deviceInfoRepository.leaveTeam(deviceCode)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorMessage = "Error " + response.code().toString()
                    Log.d(SourceDataRepository.TAG, errorMessage)
                    onError()
                }
            }
        }
    }

    override fun getDeviceInfo(session: Session) {
        viewModelScope.launch {
            val response = deviceInfoRepository.getDeviceStatus()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val value = response.body()?.value
                    val organization = value?.organizationName
                    val organizationKey = value?.organizationCode
                    val label = value?.label ?: ""
                    session.saveDeviceName(label)
                    session.saveOrganization(organization)
                    session.saveOrganizationCode(organizationKey)
                } else if (response.code() == 404) {
                    val jsonString = response.errorBody()!!.charStream()
                    val errorResponse =
                        Gson().fromJson(jsonString, ResponseDeviceInfo::class.java)
                    if (errorResponse?.error?.status == "ENTITY_NOT_FOUND_ERROR")
                        uiHandler.showToast(errorResponse.error?.status!!)
                } else {
                    val errorMessage = "Error " + response.code().toString()
                    uiHandler.showToast("$errorMessage fetching device info")
                }
            }
        }
    }

    override fun updateDeviceInfo(session: Session) {
        viewModelScope.launch {
            val deviceInfo = DeviceInfo(
                label = session.deviceName,
                deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
                deviceOsVersion = "${Build.VERSION.SDK_INT}",
                deviceAppVersion = BuildConfig.VERSION_NAME,
            )
            val response = deviceInfoRepository.updateDeviceInfo(deviceInfo)
            withContext(Dispatchers.Main) {
                if (response.code() > 250) {
                    uiHandler.showToast("Error ${response.code()} when update device info to server")
                }
            }
        }
    }

    override fun saveServerUrl(session: Session, url: String) {
        viewModelScope.launch {
            if (url.isEmpty() || !url.isValidHttpUrl()) {
                uiHandler.showToast("Url is not valid")
            } else {
                session.saveServerUrl(url)
                _navigateTo.value = Event(MainScreen.Home)
            }
        }
    }

    interface UIHandler {
        fun showToast(text: String)
    }
}


class HomeVMPrev : HomeViewModel() {
    override fun goToDetailScreen() {}
    override fun goToSettingScreen() {}
    override fun goToLicencesScreen() {}
    override fun goToAdvanceSettings() {}
    override fun onSearchInputChanged(searchInput: String) {}
    override fun showNotification(typeOfNotification: Int) {}
    override fun showBadge(typeOfBadge: Int) {}
    override fun syncWithServer(typeData: Int) {}
    override fun leaveTeam(onSuccess: () -> Unit, onError: () -> Unit, deviceCode: String) {}
    override fun getDeviceInfo(session: Session) {}
    override fun updateDeviceInfo(session: Session) {}
    override fun saveServerUrl(session: Session, url: String) {}
}