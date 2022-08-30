package id.worx.device.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.WelcomeScreen
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
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

    fun joinTeam () {
        _navigateTo.value = Event(WelcomeScreen.WaitingVerification)
    }

    fun backToJoinRequest(){
        _navigateTo.value = Event(WelcomeScreen.Welcome)
    }

    fun makeNewRequest() {
        _navigateTo.value = Event(WelcomeScreen.JoinTeam)
    }
}
