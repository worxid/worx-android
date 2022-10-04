package id.worx.device.client.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.data.database.Session
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    session: Session
) : ViewModel() {
    private val _theme = mutableStateOf(session.theme ?: "System default")
    val theme: MutableState<String> = _theme

    fun onThemeChanged(newTheme: String = "System default") {
        _theme.value = newTheme
    }
}