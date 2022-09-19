package id.worx.device.client.screen

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModel

@Composable
fun DetailFormScreen(
    viewModel: DetailFormViewModel,
    cameraViewModel: CameraViewModel,
    onBackNavigation: () -> Unit,
    homeViewModel: HomeViewModel
) {
    val uistate = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = onBackNavigation,
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
            homeViewModel
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewDetail() {
    val viewModel: DetailFormViewModel = hiltViewModel()
    val cameraViewModel: CameraViewModel = hiltViewModel()
    val homeViewModel: HomeViewModel = hiltViewModel()
    WorxTheme() {
        DetailFormScreen(
            viewModel = viewModel,
            cameraViewModel,
            {},
            homeViewModel
        )
    }
}