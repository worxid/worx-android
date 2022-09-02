package id.worx.device.client.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.model.Component
import id.worx.device.client.model.Form
import id.worx.device.client.repository.HomeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Home route.
 *
 * This is derived from [HomeViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val searchInput: String

    /**
     * There are no forms on data.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoForms(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : HomeUiState

    /**
     * There are forms to render, as contained in [list].
     *
     * There is guaranteed to be a [selectedForm], which is one of the posts from [list].
     */
    data class HasForms(
        val list: List<Form>,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : HomeUiState
}

/**
 * An internal representation of the Home route state, in a raw form
 */
private data class HomeViewModelState(
    val formList: List<Form>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
) {

    /**
     * Converts this [HomeViewModelState] into a more strongly typed [HomeUiState] for driving
     * the ui.
     */
    fun toUiState(): HomeUiState =
        if (formList == null) {
            HomeUiState.NoForms(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            HomeUiState.HasForms(
                list = formList,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private val _selectedForm = MutableLiveData<Form>()
    val selectedForm: LiveData<Form> = _selectedForm

    private val com1 = Component("1","")
    private val com2 = Component("2","")
    private val com3 = Component("3","")
    private val com4 = Component("4","")
    private val com5 = Component("5", "")
    private val com6 = Component("6", "")
    private val com7 = Component("7", "")
    val list = listOf(
        Form("0", arrayListOf(com1, com2, com3, com4, com5, com6, com7), "Valid Form0", "Ini adalah deskripsi"),
        Form("1", arrayListOf(com1, com2, com3, com4), "Valid Form1", "Ini adalah deskripsi"),
        Form("2", arrayListOf(com1, com2, com3, com4), "Valid Form2", "Ini adalah deskripsi"),
        Form("3", arrayListOf(com1, com2, com3, com4), "Valid Form3", "Ini adalah deskripsi")
    )

    val list2 = listOf(
        Form("1", arrayListOf(com1, com2, com3, com4), "Valid Form0", "Ini adalah deskripsi"),
        Form("2", arrayListOf(com1, com2, com3, com4), "Valid Form1", "Ini adalah deskripsi")
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshData()
    }

    fun onClickItem(form: Form) {
        _selectedForm.value = form
        _navigateTo.value = Event(MainScreen.Detail)
    }

    /**
     * Refresh posts and update the UI state accordingly
     */
    private fun refreshData() {
        // Ui state is refreshing
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = repository.refreshData()
            viewModelState.update {
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
                it.copy(list, isLoading = false)
            }
        }
    }

    /**
     * Notify that an error was displayed on the screen
     */
    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it == "Error $errorId" }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    /**
     * Notify that the user updated the search query
     */
    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
    }

}