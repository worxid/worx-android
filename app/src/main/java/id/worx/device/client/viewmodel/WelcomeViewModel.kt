package id.worx.device.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.WelcomeScreen
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.JoinTeamForm
import id.worx.device.client.model.NewTeamForm
import id.worx.device.client.model.ResponseDeviceInfo
import id.worx.device.client.repository.DeviceInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val deviceInfoRepository: DeviceInfoRepository,
) : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<WelcomeScreen>>()
    val navigateTo: LiveData<Event<WelcomeScreen>> = _navigateTo

    fun createNewTeam() {
        _navigateTo.value = Event(WelcomeScreen.CreateTeam)
    }

    fun joinExistingTeam() {
        _navigateTo.value = Event(WelcomeScreen.JoinTeam)
    }

    fun submitNewTeam() {
        _navigateTo.value = Event(WelcomeScreen.CreateTeamSubmitted)
    }

    fun goToAdvancedSetting() {
        _navigateTo.value = Event(WelcomeScreen.AdvancedSetting)
    }

    fun saveServerUrl(session: Session, url: String) {
        viewModelScope.launch {
            session.saveServerUrl(url)
        }
    }

    fun resendEmail() {}

    fun createTeam(newTeamForm: NewTeamForm, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = deviceInfoRepository.createNewTeam(newTeamForm)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    submitNewTeam()
                } else if (response.code() == 404) {
                    val jsonString = response.errorBody()!!.charStream()
                    val errorResponse = Gson().fromJson(jsonString, ResponseDeviceInfo::class.java)
                    if (errorResponse?.success == false)
                        errorResponse.error?.message?.let { onError(it) }
                } else {
                    onError("Something Error")
                }
            }
        }
    }

    fun joinTeam(
        onError: (String) -> Unit,
        joinTeamForm: JoinTeamForm
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = deviceInfoRepository.joinTeam(joinTeamForm)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _navigateTo.value = Event(WelcomeScreen.WaitingVerification)
                } else if (response.code() == 404) {
                    val jsonString = response.errorBody()!!.charStream()
                    val errorResponse = Gson().fromJson(jsonString, ResponseDeviceInfo::class.java)
                    if (errorResponse?.success == false)
                        errorResponse.error?.message?.let { onError(it) }
                } else {
                    onError("Something Error")
                }
            }
        }
    }

    fun backToJoinRequest() {
        _navigateTo.value = Event(WelcomeScreen.Welcome)
    }

    fun makeNewRequest() {
        _navigateTo.value = Event(WelcomeScreen.JoinTeam)
    }

    lateinit var uiHandler: UIHandler

    private var _deviceStatus = MutableLiveData<String?>()
    val deviceStatus: LiveData<String?> = _deviceStatus

    // temporary solution for checking device status
    fun getDeviceStatus(session: Session) {
        viewModelScope.launch {
            val response = deviceInfoRepository.getDeviceStatus()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val value = response.body()?.value
                    val deviceStatus = value?.deviceStatus
                    val organization = value?.organizationName
                    val organizationKey = value?.organizationCode
                    session.saveOrganization(organization)
                    session.saveOrganizationCode(organizationKey)
                    _deviceStatus.postValue(deviceStatus)
                } else if (response.code() == 404) {
                    val jsonString = response.errorBody()!!.charStream()
                    val errorResponse = Gson().fromJson(jsonString, ResponseDeviceInfo::class.java)
                    if (errorResponse?.error?.status == "ENTITY_NOT_FOUND_ERROR")
                        _deviceStatus.postValue(errorResponse.error?.status)
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
