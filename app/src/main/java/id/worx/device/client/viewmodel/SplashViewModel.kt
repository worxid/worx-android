package id.worx.device.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.model.ResponseDeviceInfo
import id.worx.device.client.repository.DeviceInfoRepository
import id.worx.device.client.repository.SourceDataRepository
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

    private var _deviceStatus= MutableLiveData<String?>()
    val deviceStatus: LiveData<String?> = _deviceStatus

    fun getDeviceStatus(){
        viewModelScope.launch {
             val response = deviceInfoRepository.getDeviceStatus()
            withContext(Dispatchers.Main){
                if (response.isSuccessful){
                    val deviceStatus = response.body()?.value?.deviceStatus
                    _deviceStatus.postValue("ONLINE")
                } else if (response.code() == 404 ){
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

    fun checkDatabase() {
        viewModelScope.launch {
            sourceDataRepository.getAllFormFromDB().collect {
                if (it.isNotEmpty()){
                    _deviceStatus.postValue("ONLINE")
                }
            }
        }
    }

    interface UIHandler {
        fun showToast(text: String)
    }
}