package id.worx.device.client.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.data.api.SyncServer
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.ResponseDeviceInfo
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.repository.DeviceInfoRepository
import id.worx.device.client.repository.SourceDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    var searchInput: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val repository: SourceDataRepository,
    private val syncServerWork: SyncServer
) : ViewModel() {

    val uiState = MutableStateFlow(HomeUiState())
    lateinit var uiHandler: UIHandler

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private val _showNotification = MutableStateFlow(0)
    val showNotification: StateFlow<Int> = _showNotification

    private val _showBadge = MutableStateFlow(0)
    val showBadge: StateFlow<Int> = _showBadge

    init {
        refreshData()
    }

    fun goToDetailScreen() {
        _navigateTo.value = Event(MainScreen.Detail)
    }

    fun goToSettingScreen() {
        _navigateTo.value = Event(MainScreen.Settings)
    }

    fun goToLicencesScreen() {
        _navigateTo.value = Event(MainScreen.Licences)
    }

    /**
     * Refresh data and update the UI state accordingly
     */
    private fun refreshData() {
        // Ui state is refreshing
        uiState.value.isLoading = true

        viewModelScope.launch {
            repository.getAllDraftForm().collect { list ->
                uiState.update {
                    it.copy(drafts = list)
                }
            }
        }

        viewModelScope.launch {
            repository.getAllSubmission().collect { list ->
                uiState.update {
                    it.copy(submission = list)
                }
            }
        }

        viewModelScope.launch {
            repository.getAllFormFromDB().collect { list ->
                uiState.update {
                    it.copy(list = list, isLoading = false)
                }
            }
            uiState.value.isLoading = false
            uiState.value.errorMessages = ""
        }
    }

    /**
     * Notify that the user updated the search query
     */
    fun onSearchInputChanged(searchInput: String) {
        uiState.value.searchInput = searchInput
    }

    /**
     * Show dialog of notification on the screen
     * Params : typeOfNotification that need to be shown
     */
    fun showNotification(typeOfNotification: Int) {
        _showNotification.value = typeOfNotification
    }

    /**
     * Show badge if there is new draft or submission saved
     * Params : string resources R.String.draft or R.string.submission
     */
    fun showBadge(typeOfBadge: Int) {
        _showBadge.value = typeOfBadge
    }

    fun syncWithServer(typeData : Int, viewLifecycleOwner: LifecycleOwner){
        viewModelScope.launch {
            syncServerWork.syncWithServer(typeData, viewLifecycleOwner) { refreshData() }
        }
    }

    fun leaveTeam(
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

    fun getDeviceInfo(session: Session) {
        viewModelScope.launch {
            val response = deviceInfoRepository.getDeviceStatus()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val value = response.body()?.value
                    val organization = value?.organizationName
                    val organizationKey = value?.organizationCode
                    session.saveOrganization(organization)
                    session.saveOrganizationCode(organizationKey)
                } else if (response.code() == 404) {
                    val jsonString = response.errorBody()!!.charStream()
                    val errorResponse = Gson().fromJson(jsonString, ResponseDeviceInfo::class.java)
                    if (errorResponse?.error?.status == "ENTITY_NOT_FOUND_ERROR")
                        uiHandler.showToast(errorResponse.error?.status!!)
                } else {
                    val errorMessage = "Error " + response.code().toString()
                    uiHandler.showToast("$errorMessage fetching device info")
                }
            }
        }
    }

    interface UIHandler {
        fun showToast(text: String)
    }
}