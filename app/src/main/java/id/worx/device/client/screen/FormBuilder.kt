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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.Fields
import id.worx.device.client.model.SeparatorValue
import id.worx.device.client.model.TextFieldValue
import id.worx.device.client.model.Type
import id.worx.device.client.screen.components.*
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun ValidFormBuilder(
    componentList: List<Fields>,
    viewModel: DetailFormViewModel,
    cameraViewModel: CameraViewModel,
    session: Session,
    onEvent: (DetailFormEvent) -> Unit
) {
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showDraftDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        DetailForm(
            componentList,
            viewModel,
            cameraViewModel,
            session
        )
        { showSubmitDialog = true }
        if (showSubmitDialog) {
            DialogSubmitForm(
                viewModel,
                session,
                {
                    onEvent(DetailFormEvent.SubmitForm)
                    showSubmitDialog = false
                },
                { showDraftDialog = true })
        }
        if (showDraftDialog) {
            DialogDraftForm(
                { onEvent(DetailFormEvent.SaveDraft) },
                { showDraftDialog = false })
        }
    }
}

@Composable
fun DetailForm(
    componentList: List<Fields>,
    viewModel: DetailFormViewModel,
    cameraViewModel: CameraViewModel,
    session: Session,
    showSubmitDialog: () -> Unit
) {
    val theme = session.theme
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
                Type.TextField.type -> {
                    val id = viewModel.uiState.collectAsState().value.detailForm?.fields?.get(index)?.id
                        ?: 0
                    val value = viewModel.uiState.collectAsState().value.values[id]
                            as TextFieldValue? ?: TextFieldValue()
                    WorxTextField(
                        label = item.label ?: "Free Text",
                        hint = "Answer",
                        inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                        initialValue = androidx.compose.ui.text.input.TextFieldValue(value.values ?: ""),
                        onValueChange = {
                            data[index].value = it
                            viewModel.setComponentData(index, TextFieldValue(values = it))
                        },
                        isDeleteTrail = true
                    )
                }
                Type.Checkbox.type -> {
                    WorxCheckBox(index, viewModel)
                }
                Type.RadioGroup.type -> {
                    WorxRadiobutton(index, viewModel)
                }
                Type.Dropdown.type -> {
                    WorxDropdown(index, viewModel)
                }
                Type.Date.type -> {
                    WorxDateInput(index, viewModel)
                }
                Type.Rating.type -> {
                    WorxRating(index, viewModel)
                }
                Type.File.type -> {
                    WorxAttachFile(index, viewModel)
                }
                Type.Photo.type -> {
                    WorxAttachImage(
                        index,
                        viewModel,
                        { cameraViewModel.navigateFromDetailScreen(index) }) {
                        viewModel.goToCameraPhoto(index)
                    }
                }
                Type.Signature.type -> {
                    WorxSignature(index, viewModel)
                }
                Type.Separator.type -> {
                    WorxSeparator()
                    viewModel.setComponentData(index, SeparatorValue())
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
                label = "Submit", modifier = Modifier.padding(vertical = 16.dp),
                theme = theme
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DialogSubmitForm(
    viewModel: DetailFormViewModel,
    session: Session,
    submitForm: () -> Unit,
    saveDraftForm: () -> Unit
) {
    val progress = viewModel.formProgress.value
    val fieldsNo = viewModel.uiState.collectAsState().value.detailForm!!.fields.size
    val theme = session.theme

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
                        progress = progress / 100.toFloat(),
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
                val fieldFilled = progress.toDouble() / 100 * fieldsNo
                Text(
                    text = "${fieldFilled.toInt()} of $fieldsNo Fields Answered",
                    style = Typography.body2.copy(Color.Black.copy(0.54f))
                )
                RedFullWidthButton(
                    onClickCallback = { submitForm() },
                    label = "Submit Form",
                    modifier = Modifier.padding(),
                    theme = theme
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
                    label = "",
                    hint = stringResource(R.string.draft_descr),
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

@Preview(name = "PreviewDetailForm", showSystemUi = true)
@Composable
fun PreviewFormComponent() {
    val viewModel: DetailFormViewModel = hiltViewModel()
    val cameraViewModel: CameraViewModel = hiltViewModel()

    //ValidFormBuilder(list, viewModel, cameraViewModel)
}