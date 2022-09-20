package id.worx.device.client.viewmodel

import androidx.lifecycle.*
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import id.worx.device.client.Event
import id.worx.device.client.MainScreen
import id.worx.device.client.WorxApplication
import id.worx.device.client.data.database.FormDownloadWorker
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.repository.HomeRepository
import id.worx.device.client.repository.SourceDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Const to view dialog/notification on the screen
private val SUBMITTED = 1
private val DRAFT = 2

data class HomeUiState(
    var list: List<EmptyForm> = emptyList(),
    var drafts: List<SubmitForm> = emptyList(),
    var isLoading: Boolean = false,
    var errorMessages: String = "",
    var searchInput: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: SourceDataRepository,
    private val homeRepository: HomeRepository
) : ViewModel() {

    val uiState = MutableStateFlow(HomeUiState())

    private val _navigateTo = MutableLiveData<Event<MainScreen>>()
    val navigateTo: LiveData<Event<MainScreen>> = _navigateTo

    private val _showNotification = MutableStateFlow(0)
    val showNotification: StateFlow<Int> = _showNotification

    init {
        refreshData()
    }

    fun goToDetailScreen() {
        _navigateTo.value = Event(MainScreen.Detail)
    }

    /**
     * Refresh data and update the UI state accordingly
     */
    private fun refreshData() {
        // Ui state is refreshing
        uiState.value.isLoading = true

        viewModelScope.launch {
            repository.getAllDraftForm().collect { list ->
                uiState.update {
                    it.copy(drafts = list)
                }
            }
        }

        viewModelScope.launch {
            repository.getAllFormFromDB().collect { list ->
                uiState.update {
                    it.copy(list = list, isLoading = false)
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

    /**
     * Show dialog of notification on the screen
     * Params : typeOfNotification that need to be shown
     */
    fun showNotification(typeOfNotification: Int){
        _showNotification.value = typeOfNotification
    }

    private var workManager = WorkManager.getInstance(WorxApplication())
    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private val formTemplateWorkInfoItems: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData("form_template")

    internal fun downloadFormTemplate(lifecycleOwner: LifecycleOwner, uiUpdate: () -> Unit) {
        val syncTemplateDBRequest = OneTimeWorkRequestBuilder<FormDownloadWorker>()
            .addTag("form_template")
            .setConstraints(networkConstraints)
            .build()

        workManager.enqueue(syncTemplateDBRequest)

        formTemplateWorkInfoItems.observe(lifecycleOwner, Observer { list ->
            val workInfo = list[0]
            if (list.isNotEmpty() && workInfo.state.isFinished) {
                uiUpdate()
                refreshData()
            }
        })
    }
}