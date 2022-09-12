package id.worx.device.client.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.model.Component
import id.worx.device.client.model.Form
import id.worx.device.client.model.InputData
import id.worx.device.client.repository.HomeRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
        val list: List<Form> = emptyList(),
        var isLoading: Boolean = false,
        var errorMessages: List<String> = emptyList(),
        var searchInput: String = ""
    )

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: HomeRepository
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
    private set

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private val com1 = Component("1", InputData("TextField"))
    private val com2 = Component("2", InputData("Check Box", listOf("Option1","Option2","Option3")))
    private val com3 = Component("3", InputData("Radio Button", listOf("Option1","Option2","Option3")))
    private val com4 = Component("4", InputData("DropDown", listOf("Option1","Option2","Option3")))
    private val com5 = Component("5", InputData("Date"))
    private val com6 = Component("6", InputData("Rating"))
    private val com7 = Component("7", InputData("File"))
    private val com8 = Component("8", InputData("Image"))
    private val com9 = Component("9", InputData("Signature"))
    val list = listOf(
        Form("0", arrayListOf(com1, com2, com3, com4, com5, com6, com7,com8, com9), "Valid Form0", "Ini adalah deskripsi"),
        Form("1", arrayListOf(com1, com2, com3, com4), "Valid Form1", "Ini adalah deskripsi"),
        Form("2", arrayListOf(com1, com2, com3, com4), "Valid Form2", "Ini adalah deskripsi"),
        Form("3", arrayListOf(com1, com2, com3, com4), "Valid Form3", "Ini adalah deskripsi")
    )

    val list2 = listOf(
        Form("1", arrayListOf(com1, com2, com3, com4), "Valid Form0", "Ini adalah deskripsi"),
        Form("2", arrayListOf(com1, com2, com3, com4), "Valid Form1", "Ini adalah deskripsi")
    )

    init {
        refreshData()
    }

    fun goToDetailScreen() {
        _navigateTo.value = Event(MainScreen.Detail)
    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    private fun refreshData() {
        // Ui state is refreshing
        uiState.isLoading = true

        viewModelScope.launch {
            val result = repository.refreshData()
//                when (result) {
//                    is Result.Success -> it.copy(list = result.data, isLoading = false)
//                    is Result.Error -> {
//                        val errorMessages = it.errorMessages + ErrorMessage(
//                            id = UUID.randomUUID().mostSignificantBits,
//                            messageId = R.string.load_error
//                        )
//                        it.copy(errorMessages = errorMessages, isLoading = false)
//                    }
//                }
                uiState.isLoading = false
            }
        }

    /**
     * Notify that an error was displayed on the screen
     */
    fun errorShown(errorId: Long) {
        uiState.errorMessages = listOf ("Error $errorId")
    }

    /**
     * Notify that the user updated the search query
     */
    fun onSearchInputChanged(searchInput: String) {
        uiState.searchInput = searchInput
    }
}