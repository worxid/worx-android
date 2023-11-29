package id.worx.mobile.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.mobile.Event
import id.worx.mobile.MainScreen
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor() : ViewModel() {

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private var _photoPath = mutableStateOf<String?>("")
    val photoPath = _photoPath

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