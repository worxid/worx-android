package id.worx.device.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.Screen
import id.worx.device.client.screen.VerificationEvent
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
) : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<Screen>>()
    val navigateTo: LiveData<Event<Screen>> = _navigateTo

    fun createNewTeam () {
        _navigateTo.value = Event(Screen.CreateTeam)
    }

    fun joinExistingTeam () {
        _navigateTo.value = Event(Screen.JoinTeam)
    }

    fun submitNewTeam () {
        _navigateTo.value = Event(Screen.CreateTeamSubmitted)
    }

    fun resendEmail(){}

    fun joinTeam () {
        _navigateTo.value = Event(Screen.WaitingVerification)
    }

    fun backToJoinRequest(){
        _navigateTo.value = Event(Screen.Welcome)
    }

    fun makeNewRequest() {
        _navigateTo.value = Event(Screen.JoinTeam)
    }
}
