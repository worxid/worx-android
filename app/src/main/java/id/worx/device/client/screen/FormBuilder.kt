package id.worx.device.client.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
import id.worx.device.client.theme.PrimaryMainGreen
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
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
                viewModel = viewModel,
                session = session,
                submitForm = {
                    validation = true
                    if (totalNonValidData == 0) {
                        onEvent(DetailFormEvent.SubmitForm)
                    }
                    showSubmitDialog = false
                },
                saveDraftForm = {
                    showSubmitDialog = false
                    showDraftDialog = true
                },
                onCancel = {
                    showSubmitDialog = false
                }
            )
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

@Composable
fun DialogSubmitForm(
    viewModel: DetailFormViewModel,
    session: Session,
    submitForm: () -> Unit,
    saveDraftForm: () -> Unit,
    onCancel: () -> Unit,
) {
    val progress = viewModel.formProgress.value
    val separatorCount =
        viewModel.uiState.collectAsState().value.detailForm?.fields?.count { it.type == Type.Separator.type }
    val fieldTotal = separatorCount?.let {
        viewModel.uiState.collectAsState().value.detailForm?.fields?.size?.minus(it) ?: 0
    } ?: 0
    val fieldFilled = viewModel.uiState.collectAsState().value.values.count { it.value != null }

    DialogSubmitForm(
        session = session,
        progress = progress,
        fieldTotal = fieldTotal,
        fieldFilled = fieldFilled,
        submitForm = submitForm,
        saveDraftForm = saveDraftForm,
        onCancel = onCancel,
    )
}

@Composable
fun DialogSubmitForm(
    session: Session,
    progress: Int,
    fieldTotal: Int,
    fieldFilled: Int,
    submitForm: () -> Unit,
    saveDraftForm: () -> Unit,
    onCancel: () -> Unit,
) {
    Dialog(
        content = {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colors.secondary),
            ) {
                val (
                    title,
                    icon,
                    description,
                    divider1,
                    submit,
                    divider2,
                    saveDraft,
                    divider3,
                    cancel,
                ) = createRefs()

                Text(
                    text = "Submit Form",
                    style = Typography.body2.copy(Color.Black.copy(0.87f)),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(parent.top, 24.dp)
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(parent.end, 16.dp)

                        width = Dimension.fillToConstraints
                    }
                )

                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .constrainAs(icon) {
                            top.linkTo(title.bottom, 16.dp)
                            start.linkTo(title.start)
                            end.linkTo(title.end)
                        }
                ) {
                    CircularProgressIndicator(
                        progress = 1f,
                        modifier = Modifier
                            .width(56.dp)
                            .height(56.dp),
                        color = Color(0xFFEAEAEA),
                        strokeWidth = 3.5.dp
                    )
                    CircularProgressIndicator(
                        progress = progress / 100.toFloat(),
                        modifier = Modifier
                            .width(56.dp)
                            .height(56.dp),
                        color = PrimaryMainGreen,
                        strokeWidth = 3.5.dp
                    )
//                    Icon(
//                        modifier = Modifier.align(Alignment.Center),
//                        painter = painterResource(R.drawable.ic_red_triangle_warning),
//                        contentDescription = "Image Warning",
//                        tint = MaterialTheme.colors.onBackground
//                    )
                }

                Text(
                    text = "$fieldFilled of $fieldTotal field answered",
                    style = Typography.body2.copy(Color.Black.copy(0.87f)),
                    modifier = Modifier.constrainAs(description) {
                        top.linkTo(icon.bottom, 16.dp)
                        start.linkTo(title.start)
                        end.linkTo(title.end)
                    }
                )

                Divider(
                    modifier = Modifier.constrainAs(divider1) {
                        top.linkTo(description.bottom, 24.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                Text(
                    text = "Submit",
                    style = Typography.body2.copy(MaterialTheme.colors.primary),
                    modifier = Modifier
                        .clickable { submitForm() }
                        .constrainAs(submit) {
                            top.linkTo(divider1.bottom, 16.dp)
                            start.linkTo(title.start)
                            end.linkTo(title.end)
                        }
                )

                Divider(
                    modifier = Modifier.constrainAs(divider2) {
                        top.linkTo(submit.bottom, 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                Text(
                    text = "Save Draft",
                    style = Typography.body2.copy(MaterialTheme.colors.onSecondary.copy(0.54f)),
                    modifier = Modifier
                        .clickable { saveDraftForm() }
                        .constrainAs(saveDraft) {
                            top.linkTo(divider2.bottom, 16.dp)
                            start.linkTo(title.start)
                            end.linkTo(title.end)
                        }
                )

                Divider(
                    modifier = Modifier.constrainAs(divider3) {
                        top.linkTo(saveDraft.bottom, 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                Text(
                    text = "Cancel",
                    style = Typography.body2.copy(MaterialTheme.colors.onSecondary.copy(0.54f)),
                    modifier = Modifier
                        .clickable { onCancel() }
                        .constrainAs(cancel) {
                            top.linkTo(divider3.bottom, 16.dp)
                            start.linkTo(title.start)
                            end.linkTo(title.end)
                            bottom.linkTo(parent.bottom, 16.dp)
                        }
                )
            }
        },
        onDismissRequest = {}
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
                    .clip(shape = RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colors.secondary)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
            ) {
                Text(
                    text = "Save draft",
                    style = Typography.body2.copy(Color.Black.copy(0.87f)),
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You can optionally add a description to the saved draft",
                    style = Typography.body2.copy(Color.Black.copy(0.87f))
                )
                WorxTextField(
                    label = "",
                    hint = stringResource(R.string.draft_descr),
                    inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = {},
                    allowMultiline = false,
                    isShowDivider = false,
                    horizontalPadding = 0.dp,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 16.dp),
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

@Preview
@Composable
fun DialogSubmitFormPreview() {
    WorxTheme {
        DialogSubmitForm(
            session = Session(LocalContext.current),
            progress = 30,
            fieldTotal = 6,
            fieldFilled = 1,
            submitForm = {},
            saveDraftForm = {},
            onCancel = {}
        )
    }
}

@Preview
@Composable
fun DialogDraftFormPreview() {
    WorxTheme {
        DialogDraftForm(
            theme = null,
            saveDraft = {},
            closeDialog = {},
        )
    }
}