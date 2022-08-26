package id.worx.device.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.Screen
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
}
