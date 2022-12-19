package id.worx.device.client.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor() : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private var _photoPath = MutableLiveData<String?>()
    val photoPath: LiveData<String?> = _photoPath

    private var _value = mutableStateOf("")
    val value = _value

    private var _type = mutableStateOf("")
    val type = _type

    private var _indexForm = MutableLiveData<Int?>()
    val indexForm: LiveData<Int?> = _indexForm

    fun setResult(value: String) {
        _value.value = value
    }

    fun navigateFromDetailScreen(indexForm : Int, type: String){
        _indexForm.value = indexForm
        _type.value = type
    }

    fun navigateToDetail() {
        _photoPath.value = null
        _indexForm.value = null
        _navigateTo.value = Event(MainScreen.Detail)
    }

    fun goToPreviewBarcode(photoPath: String, indexForm: Int) {
        _indexForm.value = indexForm
        _photoPath.value = photoPath
        _navigateTo.value = Event(MainScreen.BarcodePreview)
    }
}