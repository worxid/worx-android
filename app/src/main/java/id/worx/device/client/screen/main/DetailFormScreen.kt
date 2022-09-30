package id.worx.device.client.screen

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel

sealed class DetailFormEvent {
    object SubmitForm: DetailFormEvent()
    object SaveDraft: DetailFormEvent()
    object BackPressed: DetailFormEvent()
    object NavigateToCameraFragment: DetailFormEvent()
}

@Composable
fun DetailFormScreen(
    viewModel: DetailFormViewModel,
    cameraViewModel: CameraViewModel,
    onEvent: (DetailFormEvent) -> Unit
) {
    val uistate = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = { onEvent(DetailFormEvent.BackPressed) },
                progress = viewModel.formProgress.value,
                title = if (uistate.detailForm != null) {
                    uistate.detailForm!!.label ?: ""
                } else {
                    "Loading.."
                }
            )
        }
    ) { padding ->
        val componentList = uistate.detailForm!!.fields
        ValidFormBuilder(
            componentList = componentList,
            viewModel,
            cameraViewModel,
            onEvent
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewDetail() {
    val viewModel: DetailFormViewModel = hiltViewModel()
    val cameraViewModel: CameraViewModel = hiltViewModel()
    WorxTheme() {
        DetailFormScreen(
            viewModel = viewModel,
            cameraViewModel,
            {}
        )
    }
}