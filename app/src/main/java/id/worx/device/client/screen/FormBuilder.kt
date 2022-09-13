package id.worx.device.client.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.R
import id.worx.device.client.model.Component
import id.worx.device.client.model.InputData
import id.worx.device.client.screen.components.*
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

/*****************
 *  1 = TextField
 *  2 = Checkbox
 *  3 = Radiobutton
 *  4 = Dropdown
 ******************/
@Composable
fun ValidFormBuilder(
    componentList: List<Component>,
    viewModel: DetailFormViewModel,
    cameraViewModel: CameraViewModel
) {
    var showSubmitDialog by remember { mutableStateOf(false) }
    val formSubmitted by remember { viewModel.uiState.status }
    var showDraftDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        DetailForm(
            componentList,
            viewModel,
            cameraViewModel
        )
        { showSubmitDialog = true }
        if (showSubmitDialog) {
            DialogSubmitForm(
                viewModel,
                {
                    viewModel.submitForm()
                    showSubmitDialog = false
                },
                { showDraftDialog = true })
        }
        if (showDraftDialog) {
            DialogDraftForm(
                { viewModel.saveFormAsDraft() },
                { showDraftDialog = false })
        }
        if (formSubmitted == EventStatus.Submitted) {
            FormSubmitted() {
                viewModel.uiState.status.value = EventStatus.Done
            }
        }
    }
}

@Composable
fun DetailForm(
    componentList: List<Component>,
    viewModel: DetailFormViewModel,
    cameraViewModel: CameraViewModel,
    showSubmitDialog: () -> Unit
) {

    val data = componentList.map { component ->
        remember { mutableStateOf("") }
    }.toMutableList()
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(items = componentList) { index, item ->
            viewModel.currentComponentIndex(index)
            when (item.type) {
                "1" -> {
                    WorxTextField(
                        label = "FreeText",
                        hint = "Answer",
                        inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                        onValueChange = {
                            data[index].value = it
                            viewModel.setComponentData(index, it)
                        },
                        isDeleteTrail = true
                    )
                }
                "2" -> {
                    WorxCheckBox(index, viewModel)
                }
                "3" -> {
                    WorxRadiobutton(index, viewModel)
                }
                "4" -> {
                    WorxDropdown(index, viewModel)
                }
                "5" -> {
                    WorxDateInput(index, viewModel)
                }
                "6" -> {
                    WorxRating(index, viewModel)
                }
                "7" -> {
                    WorxAttachFile(index, viewModel)
                }
                "8" -> {
                    WorxAttachImage(
                        index,
                        viewModel,
                        { cameraViewModel.navigateFromDetailScreen(index) }) {
                        viewModel.goToCameraPhoto(
                            index
                        )
                    }
                }
                "9" -> {
                    WorxSignature(index, viewModel)
                }
                else -> {
                    Text(
                        text = "Unknown component",
                        style = Typography.body1.copy(color = Color.Black)
                    )
                }
            }
        }
        item {
            RedFullWidthButton(
                onClickCallback = { showSubmitDialog() },
                label = "Submit", modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DialogSubmitForm(
    viewModel: DetailFormViewModel,
    submitForm: () -> Unit,
    saveDraftForm: () -> Unit
) {
    val progress = (viewModel.formProgress.value / 100).toFloat()
    val fieldsNo = viewModel.uiState.detailForm!!.componentList.size

    ModalBottomSheetLayout(
        sheetState = ModalBottomSheetState(ModalBottomSheetValue.Expanded),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = "Submit",
                    style = Typography.subtitle1.copy(Color.Black)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .wrapContentSize()
                ) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .width(102.dp)
                            .height(102.dp),
                        color = PrimaryMain,
                        strokeWidth = 3.5.dp
                    )
                    Image(
                        modifier = Modifier.align(Alignment.Center),
                        painter = painterResource(R.drawable.ic_red_triangle_warning),
                        contentDescription = "Image Warning"
                    )
                }
                Text(
                    text = "${(fieldsNo * progress).toInt()} of ${fieldsNo} Fields Answered",
                    style = Typography.body2.copy(Color.Black.copy(0.54f))
                )
                RedFullWidthButton(
                    onClickCallback = { submitForm() },
                    label = "Submit Form",
                    modifier = Modifier.padding()
                )
                Text(
                    modifier = Modifier
                        .clickable { saveDraftForm() }
                        .padding(bottom = 24.dp),
                    text = "Save Draft",
                    style = Typography.button.copy(PrimaryMain)
                )
            }
        },
        content = {}
    )
}

@Composable
fun DialogDraftForm(
    saveDraft: () -> Unit,
    closeDialog: () -> Unit
) {
    Dialog(
        content = {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White)
                    .border(1.5.dp, Color.Black)
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = "Save draft?",
                    style = Typography.button.copy(Color.Black)
                )
                Text(
                    text = "You can optionally add a description to the saved draft",
                    style = Typography.body2.copy(Color.Black.copy(0.54f))
                )
                WorxTextField(
                    label = "Draft description",
                    inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {})
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(text = "Cancel",
                        style = Typography.button.copy(Color.Black.copy(0.54f)),
                        modifier = Modifier.clickable { closeDialog() })
                    Text(text = "Save",
                        style = Typography.button.copy(PrimaryMain),
                        modifier = Modifier.clickable { saveDraft() })
                }
            }
        },
        onDismissRequest = {}
    )
}

@Composable
fun FormSubmitted(
    closeNotification: () -> Unit
) {
    Dialog(
        content = {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.White)
                    .border(1.5.dp, Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Image(
                    modifier = Modifier.padding(top = 20.dp),
                    painter = painterResource(id = R.drawable.ic_tick_yellow),
                    contentDescription = "Tick"
                )
                Text(
                    text = "Successful submit form",
                    style = Typography.body2
                )
                RedFullWidthButton(
                    onClickCallback = { closeNotification() },
                    label = "Oke",
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
        },
        onDismissRequest = {}
    )
}

@Preview(name = "PreviewDetailForm", showSystemUi = true)
@Composable
fun PreviewFormComponent() {
    val viewModel: DetailFormViewModel = hiltViewModel()
    val cameraViewModel: CameraViewModel = hiltViewModel()
    val list = listOf(
//    Component("1",""),
//    Component("2",""),
//    Component("3",""),
        Component("4", InputData("WorxDropDown")),
        Component("5", InputData("Date")),
        Component("6", InputData("Rating")),
        Component("7", InputData("File")),
        Component("8", InputData("Image"))
    )

    ValidFormBuilder(list, viewModel, cameraViewModel)
}