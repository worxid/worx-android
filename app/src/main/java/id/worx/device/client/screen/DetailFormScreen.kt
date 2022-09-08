package id.worx.device.client.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun DetailFormScreen(
    viewModel: DetailFormViewModel,
    onBackNavigation: () -> Unit
) {
    val uistate = viewModel.uiState

    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = onBackNavigation,
                progress = 0,
                title = if (uistate.detailForm != null) {
                    uistate.detailForm!!.title
                } else {
                    "Loading.."
                }
            )
        }
    ) { padding ->
        if (uistate.detailForm != null) {
            val componentList = uistate.detailForm!!.componentList
            ValidFormBuilder(componentList = componentList, viewModel)
        } else {
            Text(
                modifier = Modifier.padding(padding),
                text = "Loading..",
                style = Typography.h5.copy(Color.Black)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewDetail() {
    val viewModel: DetailFormViewModel = hiltViewModel()
    WorxTheme() {
        DetailFormScreen(
            viewModel = viewModel
        ) {}
    }
}