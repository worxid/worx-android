package id.worx.device.client.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    var list: List<EmptyForm> = emptyList(),
    var isLoading: Boolean = false,
    var errorMessages: String = "",
    var searchInput: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: HomeRepository
) : ViewModel() {

    val uiState = MutableStateFlow(HomeUiState())

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

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
        uiState.value.isLoading = true

        viewModelScope.launch {
            val result = repository.fetchAllListTemplate()
            uiState.update {
                if (result.isSuccessful){
                    it.copy(list = result.body()!!.list, isLoading = false)
                } else {
                    it.copy(isLoading = false, errorMessages = "Error ${result.code()}")
                }
            }
            uiState.value.isLoading = false
            uiState.value.errorMessages = ""
        }
    }

    /**
     * Notify that the user updated the search query
     */
    fun onSearchInputChanged(searchInput: String) {
        uiState.value.searchInput = searchInput
    }
}