package id.worx.device.client.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.data.database.Session
import javax.inject.Inject

abstract class ThemeViewModel() : ViewModel() {
    abstract fun onThemeChanged(newTheme: String)
}

@HiltViewModel
class ThemeViewModelImpl @Inject constructor(
    session: Session
) : ThemeViewModel() {
    private val _theme = mutableStateOf(session.theme ?: "System default")
    val theme: MutableState<String> = _theme
    val tema : LiveData<String> = MutableLiveData(_theme.value)

    override fun onThemeChanged(newTheme: String) {
        _theme.value = newTheme
    }
}

class ThemeVMMock () : ThemeViewModel() {
    private val _theme = mutableStateOf( "System default")
    val theme: MutableState<String> = _theme

    override fun onThemeChanged(newTheme: String) {
        _theme.value = "System default"
    }
}