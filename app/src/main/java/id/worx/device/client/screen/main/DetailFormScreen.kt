package id.worx.device.client.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.R
import id.worx.device.client.data.api.SyncServer.Companion.DOWNLOADFROMSERVER
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.model.fieldmodel.Separator
import id.worx.device.client.screen.components.WorxBoxPullRefresh
import id.worx.device.client.screen.components.WorxFormSubmitButton
import id.worx.device.client.screen.components.WorxNewDropDownBottomSheet
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.theme.LocalWorxColorsPalette
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.util.getDrawableBasedOnTheme
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus
import id.worx.device.client.viewmodel.ScannerViewModel
import kotlinx.coroutines.launch

sealed class DetailFormEvent {
    object SubmitForm : DetailFormEvent()
    data class SaveDraft(val draftDescription: String) : DetailFormEvent()
    object BackPressed : DetailFormEvent()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DetailFormScreen(
    viewModel: DetailFormViewModel,
    cameraViewModel: CameraViewModel,
    scannerViewModel: ScannerViewModel,
    session: Session,
    onEvent: (DetailFormEvent) -> Unit
) {
    val uistate = viewModel.uiState.collectAsState().value
    val formStatus = viewModel.uiState.collectAsState().value.status
    val showDialogLeaveForm = remember { mutableStateOf(false) }
    var showDraftDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val detailForm = viewModel.uiState.collectAsState().value.detailForm

    val scope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { true },
        skipHalfExpanded = true
    )
    var showSubmitDialog by remember { mutableStateOf(state.isVisible) }

    BackHandler {
        showDialogLeaveForm.value =
            (formStatus == EventStatus.Filling && viewModel.formProgress.value > 0)
        if (!showDialogLeaveForm.value) {
            onEvent(DetailFormEvent.BackPressed)
        }
    }
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
    val isDraft = if (uistate.detailForm is SubmitForm) uistate.detailForm.status == 0 else false

    if (showDraftDialog) {
        DialogDraftForm(
            saveDraft = { draftDescription ->
                onEvent(DetailFormEvent.SaveDraft(draftDescription))
            },
            closeDialog = { showDraftDialog = false }
        )
    }

    DraftActionsBottomSheet(
        sheetState = sheetState,
        onDuplicate = {
            viewModel.duplicateDraft(uistate.detailForm as SubmitForm, "")
        },
        onDelete = {
            viewModel.deleteDraft(uistate.detailForm as SubmitForm, true)
        }
    ) { openBottomSheet ->
        Scaffold(
            topBar = {
                WorxTopAppBar(
                    onBack = { dispatcher.onBackPressed() },
                    progress = viewModel.formProgress.value,
                    title = if (uistate.detailForm != null) {
                        uistate.detailForm.label ?: ""
                    } else {
                        "Loading.."
                    },
                    isShowMoreOptions = isDraft,
                    onClickMoreOptions = {
                        openBottomSheet()
                    }
                )
            },
            bottomBar = {
                if (detailForm is EmptyForm || (detailForm is SubmitForm && detailForm.status == 0)) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        WorxFormSubmitButton(
                            onClickCallback = {
                                showSubmitDialog = true
                                scope.launch {
                                    state.animateTo(ModalBottomSheetValue.Expanded)
                                }
                            },
                            label = stringResource(R.string.text_submit),
                            modifier = Modifier.fillMaxWidth(),
                            buttonModifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        ) { padding ->
            val componentList = uistate.detailForm?.fields
                ?: arrayListOf(Separator().apply {
                    label = "No form"
                    description = "No forms are found. Please try to relead again"
                })

            WorxBoxPullRefresh(
                onRefresh = { viewModel.syncWithServer(DOWNLOADFROMSERVER) },
                modifier = Modifier.padding(padding)
            ) {

                ValidFormBuilder(
                    componentList = componentList,
                    viewModel,
                    cameraViewModel,
                    scannerViewModel,
                    session,
                    setDraftDialog = { showDraftDialog = it },
                    onEvent = onEvent,
                    shouldShowSubmitDialog = showSubmitDialog,
                    closeSubmitDialog = {
                        showSubmitDialog = false
                    }
                )

                if (showDialogLeaveForm.value) {
                    Dialog(onDismissRequest = { }) {
                        LeaveForm(
                            setShowDialog = { showDialogLeaveForm.value = it },
                            onDiscardClicked = {
                                viewModel.goToHome()
                            },
                            onSaveDraft = {
                                showDialogLeaveForm.value = false
                                showDraftDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = uistate.isDropdownBottomSheetShown,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(
                durationMillis = 100,
                easing = LinearEasing
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(
                durationMillis = 100,
                easing = LinearEasing
            )
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.64f)), verticalArrangement = Arrangement.Bottom
        ) {
            WorxNewDropDownBottomSheet(
                indexForm = uistate.openedIndexForm,
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DraftActionsBottomSheet(
    sheetState: ModalBottomSheetState,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    content: @Composable (openSortByBottomSheet: () -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            val colorPalette = LocalWorxColorsPalette.current
            Column(
                modifier = Modifier
                    .background(colorPalette.bottomSheetBackground)
                    .padding(bottom = 16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorPalette.bottomSheetBackground)
                        .padding(vertical = 8.dp)
                ) {
                    Canvas(modifier = Modifier
                        .size(80.dp, 4.dp)
                        .align(Alignment.Center), onDraw = {
                        drawRoundRect(
                            color = colorPalette.bottomSheetDragHandle,
                            cornerRadius = CornerRadius(2f, 2f)
                        )
                    })
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDuplicate() }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_duplicate),
                        contentDescription = "duplicate",
                        tint = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.text_duplicate),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSecondary.copy(alpha = 0.87f)
                    )
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDelete() }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "delete",
                        tint = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f),
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.text_delete),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSecondary.copy(alpha = 0.87f)
                    )
                }
            }
        },
        sheetBackgroundColor = LocalWorxColorsPalette.current.bottomSheetBackground
    ) {
        content(openSortByBottomSheet = { scope.launch { sheetState.show() } })
    }
}

@Composable
fun LeaveForm(
    setShowDialog: (Boolean) -> Unit = {},
    onDiscardClicked: () -> Unit = {},
    onSaveDraft: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.secondary),
    ) {
        val (imgQuestion, tvSubtitle, tvSaveDraft, tvDiscard, tvCancel, dividerDesc, dividerSaveDraft, dividerDiscard) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.ic_question.getDrawableBasedOnTheme(R.drawable.ic_question_white)),
            contentDescription = "question",
            modifier = Modifier
                .size(56.dp)
                .constrainAs(imgQuestion) {
                    top.linkTo(parent.top, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = stringResource(id = R.string.leave_form_sub),
            style = Typography.body2.copy(LocalWorxColorsPalette.current.textFieldColor),
            modifier = Modifier.constrainAs(tvSubtitle) {
                top.linkTo(imgQuestion.bottom, 8.dp)
                start.linkTo(parent.start, 16.dp)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.fillToConstraints
            },
            textAlign = TextAlign.Center
        )

        Divider(
            color = LocalWorxColorsPalette.current.divider,
            modifier = Modifier
                .padding(top = 16.dp)
                .constrainAs(dividerDesc) {
                    top.linkTo(tvSubtitle.bottom, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        Text(
            text = stringResource(id = R.string.save_draft),
            style = Typography.body2.copy(
                color = LocalWorxColorsPalette.current.textFieldColor,
                fontWeight = FontWeight.W500
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(tvSaveDraft) {
                    top.linkTo(dividerDesc.bottom, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.matchParent
                }
                .clickable {
                    setShowDialog(false)
                    onSaveDraft()
                }
        )

        Divider(
            color = LocalWorxColorsPalette.current.divider,
            modifier = Modifier
                .constrainAs(dividerSaveDraft) {
                    top.linkTo(tvSaveDraft.bottom, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        Text(
            text = stringResource(id = R.string.discard),
            style = Typography.body2.copy(
                color = LocalWorxColorsPalette.current.button,
                fontWeight = FontWeight.W500
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(tvDiscard) {
                    top.linkTo(dividerSaveDraft.bottom, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .clickable {
                    setShowDialog(false)
                    onDiscardClicked()
                }
        )

        Divider(
            color = LocalWorxColorsPalette.current.divider,
            modifier = Modifier.constrainAs(dividerDiscard) {
                top.linkTo(tvDiscard.bottom, 16.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            })

        Text(
            text = stringResource(id = R.string.cancel),
            style = Typography.body2.copy(
                color = LocalWorxColorsPalette.current.textFieldColor,
                fontWeight = FontWeight.W500
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(tvCancel) {
                    top.linkTo(dividerDiscard.top, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, 16.dp)
                    width = Dimension.fillToConstraints
                }
                .clickable {
                    setShowDialog(false)
                }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewDetail() {
    val viewModel: DetailFormViewModel = hiltViewModel()
    val cameraViewModel: CameraViewModel = hiltViewModel()
    val scannerViewModel: ScannerViewModel = hiltViewModel()
    WorxTheme {
        DetailFormScreen(
            viewModel = viewModel,
            cameraViewModel,
            scannerViewModel,
            Session(LocalContext.current),
            {}
        )
    }
}