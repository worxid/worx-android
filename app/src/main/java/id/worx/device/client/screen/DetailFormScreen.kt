package id.worx.device.client.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.model.Form
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.HomeViewModel

@Composable
fun DetailFormScreen(
    viewModel: HomeViewModel,
    onBackNavigation: () -> Unit
) {
    val listForm: Form? by viewModel.selectedForm.observeAsState(null)

    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = onBackNavigation,
                progress = 0,
                title = listForm?.title ?: "Loading.."
            )
        }
    ) { padding ->
        if (listForm == null) {
            Text(modifier= Modifier.padding(padding), text = "Loading..", style = Typography.h5.copy(Color.Black))
        } else {
            ValidFormBuilder(componentList = listForm!!.componentList)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewDetail() {
    val viewModel: HomeViewModel = hiltViewModel()
    WorxTheme() {
        DetailFormScreen(
            viewModel = viewModel,
            {})
    }
}