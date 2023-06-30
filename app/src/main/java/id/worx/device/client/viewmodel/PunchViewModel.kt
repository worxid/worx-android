package id.worx.device.client.viewmodel

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.WorxApplication
import id.worx.device.client.util.TimerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class
PunchViewModel @Inject constructor(
    private val application: WorxApplication,
    private val dateFormat: SimpleDateFormat
) : ViewModel() {

    private val _state = MutableStateFlow(PunchUiState())
    val state: StateFlow<PunchUiState> = _state.asStateFlow()

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private var _serviceIntent: Intent =
        Intent(application.applicationContext, TimerService::class.java)

    private val updateTimer: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        timer = intent?.getDoubleExtra(TimerService.TIME_EXTRA, 0.0) ?: 0.0
                    )
                }
            }
        }
    }

    private var _hasTimeStarted = MutableStateFlow(false)

    init {
        application.applicationContext.registerReceiver(updateTimer, IntentFilter(TimerService.TIMER_UPDATED))

        viewModelScope.launch {
            while (isActive){
                _state.update {
                    it.copy(localTime = dateFormat.format(Date()))
                }
                delay(1000)
            }
        }
    }

    fun clockIn(){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    punchStatus = PunchStatus.IN,
                )
            }
            startTimer()
            _navigateTo.value = Event(MainScreen.Home)
        }
        Log.d("Punch Time Vm", _state.value.timer.toString())
    }

    fun clockOut(){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    punchStatus = PunchStatus.OUT
                )
            }
            stopTimer()
            _navigateTo.value = Event(MainScreen.Home)
        }
    }

    private fun resetTimer() {
        viewModelScope.launch {
            stopTimer()
            _state.update {
                it.copy(timer = 0.0)
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            _serviceIntent.putExtra(TimerService.TIME_EXTRA, _state.value.timer)
            application.startService(_serviceIntent)
            // todo: manipulate the ui
            _hasTimeStarted.value = true
        }
    }

    private fun stopTimer() {
        viewModelScope.launch {
            application.stopService(_serviceIntent)
            // todo: manipulate the ui
            _hasTimeStarted.value = false
        }
    }
}

enum class PunchStatus {
    IN, OUT
}

data class PunchUiState(
    val punchStatus: PunchStatus = PunchStatus.OUT,
    val localTime: String? = null,
    val timer: Double = 0.0,
)