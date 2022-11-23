package id.worx.device.client.screen

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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.*
import id.worx.device.client.screen.components.*
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

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
    var validation by remember { mutableStateOf(false) }
    var isValid by remember { mutableStateOf(false) }
    val theme = session.theme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.secondary)
    ) {
        DetailForm(
            componentList,
            viewModel,
            cameraViewModel,
            session,
            validation,
            { isValid = it }
        )
        { showSubmitDialog = true }
        if (showSubmitDialog) {
            DialogSubmitForm(
                viewModel,
                session,
                {
                    validation = true
                    if (isValid) {
                        onEvent(DetailFormEvent.SubmitForm)
                    }
                    showSubmitDialog = false
                },
                { showDraftDialog = true })
        }
        if (showDraftDialog) {
            DialogDraftForm(
                theme = theme,
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
    validation: Boolean,
    isValid: (Boolean) -> Unit,
    showSubmitDialog: () -> Unit
) {
    val theme = session.theme
    val data = componentList.map { component ->
        remember { mutableStateOf("") }
    }.toMutableList()
    val listState = rememberLazyListState(viewModel.indexScroll.value, viewModel.offset.value)
    val formStatus = viewModel.uiState.collectAsState().value.status

    LaunchedEffect(key1 = listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            viewModel.indexScroll.value = listState.firstVisibleItemIndex
            viewModel.offset.value = listState.firstVisibleItemScrollOffset
        }
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (lazyColumn, btnSubmit) = createRefs()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .constrainAs(lazyColumn) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(btnSubmit.top)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(items = componentList) { index, item ->
                viewModel.currentComponentIndex(index)
                when (item.type) {
                    Type.TextField.type -> {
                        val id =
                            viewModel.uiState.collectAsState().value.detailForm?.fields?.get(index)?.id
                                ?: 0
                        val value = viewModel.uiState.collectAsState().value.values[id]
                                as TextFieldValue? ?: TextFieldValue()
                        val form =
                            viewModel.uiState.collectAsState().value.detailForm!!.fields.getOrNull(
                                index
                            )
                        WorxTextField(
                            theme = theme,
                            label = item.label ?: "Free Text",
                            hint = "Answer",
                            inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                            initialValue = androidx.compose.ui.text.input.TextFieldValue(
                                value.values ?: ""
                            ),
                            onValueChange = {
                                data[index].value = it
                                viewModel.setComponentData(index, TextFieldValue(values = it))
                            },
                            isDeleteTrail = !arrayListOf(
                                EventStatus.Done,
                                EventStatus.Submitted
                            ).contains(formStatus),
                            isRequired = form?.required ?: false,
                            validation = validation,
                            isValid = isValid,
                            isEnabled = !arrayListOf(
                                EventStatus.Done,
                                EventStatus.Submitted
                            ).contains(formStatus)
                        )
                    }
                    Type.Checkbox.type -> {
                        WorxCheckBox(index, viewModel, validation, isValid)
                    }
                    Type.RadioGroup.type -> {
                        WorxRadiobutton(index, viewModel, validation, isValid)
                    }
                    Type.Dropdown.type -> {
                        WorxDropdown(index, viewModel, session, validation, isValid)
                    }
                    Type.Date.type -> {
                        WorxDateInput(index, viewModel, session, validation, isValid)
                    }
                    Type.Rating.type -> {
                        WorxRating(index, viewModel, validation, isValid)
                    }
                    Type.File.type -> {
                        WorxAttachFile(index, viewModel, session, validation, isValid)
                    }
                    Type.Photo.type -> {
                        WorxAttachImage(
                            index,
                            viewModel,
                            session,
                            { cameraViewModel.navigateFromDetailScreen(index) }, validation, isValid
                        ) {
                            viewModel.goToCameraPhoto(index)
                        }
                    }
                    Type.Signature.type -> {
                        WorxSignature(index, viewModel, session)
                    }
                    Type.Separator.type -> {
                        WorxSeparator(index, viewModel, session)
                    }
                    else -> {
                        Text(
                            text = "Unknown component",
                            style = Typography.body1.copy(color = Color.Black)
                        )
                    }
                }
            }
        }

//        val detailForm = viewModel.uiState.value.detailForm
//        if (detailForm is EmptyForm || (detailForm is SubmitForm && detailForm.status == 0)) {
//            item {
//                RedFullWidthButton(
//                    onClickCallback = { showSubmitDialog() },
//                    label = "Submit", modifier = Modifier.padding(vertical = 16.dp),
//                    theme = theme
//                )
//            }
//        }
        RedFullWidthButton(
            onClickCallback = { showSubmitDialog() },
            label = "Submit",
            modifier = Modifier
                .padding(vertical = 16.dp)
                .constrainAs(btnSubmit) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            theme = theme
        )
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
                    .background(MaterialTheme.colors.secondary),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = "Submit",
                    style = Typography.subtitle1.copy(MaterialTheme.colors.onSecondary)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .wrapContentSize()
                ) {
                    CircularProgressIndicator(
                        progress = 1f,
                        modifier = Modifier
                            .width(102.dp)
                            .height(102.dp),
                        color = Color(0xFFEAEAEA),
                        strokeWidth = 3.5.dp
                    )
                    CircularProgressIndicator(
                        progress = progress / 100.toFloat(),
                        modifier = Modifier
                            .width(102.dp)
                            .height(102.dp),
                        color = MaterialTheme.colors.onBackground,
                        strokeWidth = 3.5.dp
                    )
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        painter = painterResource(R.drawable.ic_red_triangle_warning),
                        contentDescription = "Image Warning",
                        tint = MaterialTheme.colors.onBackground
                    )
                }
                val fieldFilled = progress.toDouble() / 100 * fieldsNo
                Text(
                    text = "${fieldFilled.toInt()} of $fieldsNo Fields Answered",
                    style = Typography.body2.copy(MaterialTheme.colors.onSecondary.copy(0.54f))
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
                    style = Typography.button.copy(MaterialTheme.colors.onBackground)
                )
            }
        },
        content = {}
    )
}

@Composable
fun DialogDraftForm(
    theme: String?,
    saveDraft: () -> Unit,
    closeDialog: () -> Unit
) {
    Dialog(
        content = {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(MaterialTheme.colors.secondary)
                    .border(1.5.dp, Color.Black)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 20.dp),
                    text = "Save draft?",
                    style = Typography.button.copy(MaterialTheme.colors.onSecondary)
                )
                Text(
                    text = "You can optionally add a description to the saved draft",
                    style = Typography.body2.copy(MaterialTheme.colors.onSecondary.copy(0.54f))
                )
                WorxTextField(
                    theme = theme,
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
                        style = Typography.button.copy(MaterialTheme.colors.onSecondary.copy(0.54f)),
                        modifier = Modifier.clickable { closeDialog() })
                    Text(text = "Save",
                        style = Typography.button.copy(MaterialTheme.colors.onBackground),
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