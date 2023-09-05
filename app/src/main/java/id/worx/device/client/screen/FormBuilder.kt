package id.worx.device.client.screen

import android.util.Log
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
import id.worx.device.client.MainScreen
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.Fields
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.model.Type
import id.worx.device.client.model.fieldmodel.TextField
import id.worx.device.client.model.fieldmodel.TextFieldValue
import id.worx.device.client.screen.components.*
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus
import id.worx.device.client.viewmodel.ScannerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ValidFormBuilder(
    componentList: List<Fields>,
    viewModel: DetailFormViewModel,
    cameraViewModel: CameraViewModel,
    scannerViewModel: ScannerViewModel,
    session: Session,
    onEvent: (DetailFormEvent) -> Unit,
) {
    var showDraftDialog by remember { mutableStateOf(false) }
    var validation by remember { mutableStateOf(false) }
    var isValid by remember { mutableStateOf(false) }
    val theme = session.theme
    val scope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { true },
        skipHalfExpanded = true
    )
    var showSubmitDialog by remember { mutableStateOf(state.isVisible) }
    val totalNonValidData = componentList.filter { !(it.isValid ?: true) }.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.secondary)
    ) {
        DetailForm(
            componentList,
            viewModel,
            cameraViewModel,
            scannerViewModel,
            session,
            validation,
            showSubmitDialog = {
                showSubmitDialog = true
                scope.launch {
                    state.animateTo(ModalBottomSheetValue.Expanded)
                }
            }
        )

        if (showSubmitDialog) {
            DialogSubmitForm(
                viewModel,
                session,
                state,
                {
                    validation = true
                    if (totalNonValidData == 0) {
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
    scannerViewModel: ScannerViewModel,
    session: Session,
    validation: Boolean,
    showSubmitDialog: () -> Unit,
) {
    val theme = session.theme
    val listState = rememberLazyListState(viewModel.indexScroll.value, viewModel.offset.value)
    val formStatus = viewModel.uiState.collectAsState().value.status
    val detailForm = viewModel.uiState.collectAsState().value.detailForm

    LaunchedEffect(key1 = listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            viewModel.indexScroll.value = listState.firstVisibleItemIndex
            viewModel.offset.value = listState.firstVisibleItemScrollOffset
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
            .fillMaxSize()
    ) {
        val (lazyColumn, btnSubmit) = createRefs()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .constrainAs(lazyColumn) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .navigationBarsPadding()
                .imePadding()
                .padding(vertical = 12.dp)
            ,
            contentPadding = WindowInsets.statusBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                .asPaddingValues()
        ) {
            itemsIndexed(items = componentList) { index, item ->
                when (item.type) {
                    Type.TextField.type -> {
                        val id =
                            viewModel.uiState.collectAsState().value.detailForm?.fields?.get(index)?.id
                                ?: 0
                        val value = viewModel.uiState.collectAsState().value.values[id]
                                as TextFieldValue? ?: TextFieldValue()
                        val form =
                            viewModel.uiState.collectAsState().value.detailForm?.fields?.getOrNull(
                                index
                            )
                        val textField = form as TextField?
                        Log.d("MUTILINE", textField.toString())

                        WorxTextField(
                            label = item.label ?: "Free Text",
                            hint = "Answer",
                            inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                            initialValue = androidx.compose.ui.text.input.TextFieldValue(
                                value.values ?: ""
                            ),
                            onValueChange = {
                                if (it.isNullOrEmpty()) {
                                    viewModel.setComponentData(index, null)
                                } else {
                                    viewModel.setComponentData(index, TextFieldValue(values = it))
                                }
                            },
                            isDeleteTrail = !arrayListOf(
                                EventStatus.Done,
                                EventStatus.Submitted
                            ).contains(formStatus),
                            isRequired = form?.required ?: false,
                            validation = validation,
                            isEnabled = !arrayListOf(
                                EventStatus.Done,
                                EventStatus.Submitted
                            ).contains(formStatus),
                            allowMultiline = textField?.allowMultiline ?: false,
                            viewModel = viewModel,
                            index = index
                        )
                    }
                    Type.Checkbox.type -> {
                        WorxCheckBox(index, viewModel, validation, session)
                    }
                    Type.RadioGroup.type -> {
                        WorxRadiobutton(index, viewModel, validation, session)
                    }
                    Type.Dropdown.type -> {
                        WorxDropdown(index, viewModel, session, validation)
                    }
                    Type.Date.type -> {
                        WorxDateInput(index, viewModel, session, validation)
                    }
                    Type.Rating.type -> {
                        WorxRating(index, viewModel, validation, session)
                    }
                    Type.File.type -> {
                        WorxAttachFile(index, viewModel, session, validation)
                    }
                    Type.Photo.type -> {
                        WorxAttachImage(
                            index,
                            viewModel,
                            session,
                            { cameraViewModel.navigateFromDetailScreen(index) }, validation
                        ) {
                            viewModel.goToCameraPhoto(index, MainScreen.Detail)
                        }
                    }
                    Type.Signature.type -> {
                        WorxSignature(index, viewModel, session, validation)
                    }
                    Type.Separator.type -> {
                        WorxSeparator(index, viewModel, session)
                    }
                    Type.BarcodeField.type -> {
                        WorxBarcodeField(index, viewModel, scannerViewModel, session, validation)
                    }
                    Type.Time.type -> {
                        WorxTimeInput(index, viewModel, session)
                    }
                    Type.Boolean.type -> {
                        WorxBooleanField(index, viewModel, validation, session)
                    }
                    Type.Integer.type -> {
                        WorxIntegerField(index, viewModel, session)
                    }
                    Type.Sketch.type -> {
                        WorxSketch(indexForm = index,
                            viewModel = viewModel,
                            session = session,
                            validation = validation)
                    }
                    else -> {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            Text(
                                text = "Unknown component ${item.type}",
                                style = Typography.body1.copy(color = Color.Black)
                            )
                            Divider(color = GrayDivider,
                                modifier = Modifier.padding(vertical = 16.dp))
                        }
                    }
                }
            }
        }

        if (detailForm is EmptyForm || (detailForm is SubmitForm && detailForm.status == 0)) {
            WorxFormSubmitButton(
                onClickCallback = { showSubmitDialog() },
                label = "Submit",
                modifier = Modifier
                    .constrainAs(btnSubmit) {
                        bottom.linkTo(parent.bottom, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                    },
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
    state: ModalBottomSheetState,
    submitForm: () -> Unit,
    saveDraftForm: () -> Unit,
) {
    val progress = viewModel.formProgress.value
    val separatorCount =
        viewModel.uiState.collectAsState().value.detailForm?.fields?.count { it.type == Type.Separator.type }
    val fieldsNo = separatorCount?.let {
        viewModel.uiState.collectAsState().value.detailForm?.fields?.size?.minus(
            it)
    }
    val theme = session.theme

    ModalBottomSheetLayout(
        sheetState = state,
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
                val fieldFilled =
                    viewModel.uiState.collectAsState().value.values.count { it.value != null }
                Text(
                    text = "$fieldFilled of $fieldsNo Fields Answered",
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
    closeDialog: () -> Unit,
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
                    label = "",
                    hint = stringResource(R.string.draft_descr),
                    inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {},
                    allowMultiline = false
                )
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