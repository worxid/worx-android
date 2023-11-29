package id.worx.mobile.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.mobile.data.database.Session
import id.worx.mobile.screen.main.AppTheme
import javax.inject.Inject

abstract class ThemeViewModel : ViewModel() {
    abstract fun onThemeChanged(newTheme: String)
}

@HiltViewModel
class ThemeViewModelImpl @Inject constructor(
    session: Session
) : ThemeViewModel() {
    private val _theme = mutableStateOf(session.theme)
    val theme: MutableState<String> = _theme

    override fun onThemeChanged(newTheme: String) {
        _theme.value = newTheme
    }
}

class ThemeVMMock : ThemeViewModel() {
    private val _theme = mutableStateOf(AppTheme.DEVICE_SYSTEM.value)
    val theme: MutableState<String> = _theme

    override fun onThemeChanged(newTheme: String) {
        _theme.value = AppTheme.DEVICE_SYSTEM.value
    }
}