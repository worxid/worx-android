package id.worx.mobile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.mobile.model.ResponseDeviceInfo
import id.worx.mobile.repository.DeviceInfoRepository
import id.worx.mobile.repository.SourceDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val deviceInfoRepository: DeviceInfoRepository,
    private val sourceDataRepository: SourceDataRepository
) : ViewModel() {

    lateinit var uiHandler: UIHandler

    private var _deviceStatus = MutableLiveData<String?>()
    val deviceStatus: LiveData<String?> = _deviceStatus

    fun getDeviceStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = deviceInfoRepository.getDeviceStatus()
            if (response.isSuccessful) {
                val deviceStatus = response.body()?.value?.deviceStatus
                withContext(Dispatchers.Main) {
                    _deviceStatus.postValue(deviceStatus)
                }
            } else if (response.code() == 404) {
                val jsonString = response.errorBody()!!.charStream()
                val errorResponse = Gson().fromJson(jsonString, ResponseDeviceInfo::class.java)
                if (errorResponse?.error?.status == "ENTITY_NOT_FOUND_ERROR") {
                    withContext(Dispatchers.Main) {
                        _deviceStatus.postValue(errorResponse.error?.status)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    val errorMessage = "Error " + response.code().toString()
                    uiHandler.showToast("$errorMessage fetching device info")
                }
            }
        }
    }

    fun checkDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val forms = sourceDataRepository.getAllFormFromDB()
            if (forms.isNotEmpty()) {
                _deviceStatus.postValue("APPROVED")
            }
        }
    }

    interface UIHandler {
        fun showToast(text: String)
    }
}