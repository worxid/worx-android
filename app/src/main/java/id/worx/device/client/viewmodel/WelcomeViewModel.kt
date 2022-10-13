package id.worx.device.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.WelcomeScreen
import id.worx.device.client.model.JoinTeamForm
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

    fun createNewTeam () {
        _navigateTo.value = Event(WelcomeScreen.CreateTeam)
    }

    fun joinExistingTeam () {
        _navigateTo.value = Event(WelcomeScreen.JoinTeam)
    }

    fun submitNewTeam () {
        _navigateTo.value = Event(WelcomeScreen.CreateTeamSubmitted)
    }

    fun resendEmail(){}

    fun joinTeam(
        onError: (String) -> Unit,
        joinTeamForm: JoinTeamForm
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = deviceInfoRepository.joinTeam(joinTeamForm)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _navigateTo.value = Event(WelcomeScreen.WaitingVerification)
                } else if (response.code() == 404){
                    val jsonString = response.errorBody()!!.charStream()
                    val errorResponse = Gson().fromJson(jsonString, ResponseDeviceInfo::class.java)
                    if (errorResponse?.success == false)
                        errorResponse.error?.message?.let { onError(it) }
                }
                else {
                    onError("Something Error")
                }
            }
        }
    }

    fun backToJoinRequest(){
        _navigateTo.value = Event(WelcomeScreen.Welcome)
    }

    fun makeNewRequest() {
        _navigateTo.value = Event(WelcomeScreen.JoinTeam)
    }
}
