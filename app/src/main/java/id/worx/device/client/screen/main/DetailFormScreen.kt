package id.worx.device.client.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.R
import id.worx.device.client.data.api.SyncServer.Companion.DOWNLOADFROMSERVER
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.model.fieldmodel.Separator
import id.worx.device.client.screen.components.WorxAnimatedFabButton
import id.worx.device.client.screen.components.WorxBoxPullRefresh
import id.worx.device.client.screen.components.WorxDialog
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus
import id.worx.device.client.viewmodel.ScannerViewModel
import kotlinx.coroutines.launch

sealed class DetailFormEvent {
    object SubmitForm : DetailFormEvent()
    object SaveDraft : DetailFormEvent()
    object BackPressed : DetailFormEvent()
    object NavigateToCameraFragment : DetailFormEvent()
    object NavigateToSelectionMenuFragment: DetailFormEvent()
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
    val lifecycleOwner = LocalLifecycleOwner.current

    val coroutineScope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = false,
        confirmStateChange = {
            true
        }
    )
    val showSubmitDialog = remember { mutableStateOf(bottomSheetState.isVisible) }

    val listState = rememberLazyListState()
    val expandedFab = remember {
        mutableStateOf(true)
    }
    val expandedFabState = remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    LaunchedEffect(key1 = expandedFabState.value) {
        expandedFab.value = expandedFabState.value
    }

    BackHandler {
        showDialogLeaveForm.value =
            (formStatus == EventStatus.Filling && viewModel.formProgress.value > 0)
        if (!showDialogLeaveForm.value) {
            onEvent(DetailFormEvent.BackPressed)
        }
    }
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = { dispatcher.onBackPressed() },
                progress = viewModel.formProgress.value,
                title = if (uistate.detailForm != null) {
                    uistate.detailForm!!.label ?: ""
                } else {
                    "Loading.."
                }
            )
        },
        floatingActionButton = {
            if (uistate.detailForm is EmptyForm || (uistate.detailForm is SubmitForm && uistate.detailForm.status == 0)){
                WorxAnimatedFabButton(
                    text = {
                        Text(text = "Submit")
                    },
                    icon = {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    },
                    onClickEvent = {
                        showSubmitDialog.value = true
                        coroutineScope.launch {
                            bottomSheetState.show()
                        }
                    },
                    expanded = expandedFab.value
                )
            }
        }
    ) { padding ->
        val componentList = uistate.detailForm?.fields
            ?: arrayListOf(Separator().apply {
                label = "No form"
                description = "No forms are found. Please try to relead again"
            })

        WorxBoxPullRefresh(
            onRefresh = { viewModel.syncWithServer(DOWNLOADFROMSERVER, lifecycleOwner) }
        ) {

            ValidFormBuilder(
                componentList = componentList,
                viewModel,
                cameraViewModel,
                scannerViewModel,
                session,
                onEvent,
                listState,
                bottomSheetState,
                showSubmitDialog
            )

            if (showDialogLeaveForm.value) {
                WorxDialog(content = {
                    LeaveForm(
                        setShowDialog = { showDialogLeaveForm.value = it },
                        onPositiveButton = {
                            viewModel.goToHome()
                        }
                    )
                }, setShowDialog = { showDialogLeaveForm.value = it })
            }
        }
    }
}

@Composable
fun LeaveForm(
    setShowDialog: (Boolean) -> Unit = {},
    onPositiveButton: () -> Unit = {}
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.secondary)
    ) {
        val (tvTitle, tvSubtitle, tvYes, tvCancel) = createRefs()

        Text(
            text = stringResource(id = R.string.leave_form),
            style = Typography.body2.copy(
                MaterialTheme.colors.onSecondary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.constrainAs(tvTitle) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = stringResource(id = R.string.leave_form_sub),
            style = Typography.body2.copy(
                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f),
                fontSize = 12.sp
            ),
            modifier = Modifier.constrainAs(tvSubtitle) {
                top.linkTo(tvTitle.bottom, 20.dp)
                start.linkTo(tvTitle.start)
                end.linkTo(tvTitle.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = stringResource(id = R.string.leave),
            style = Typography.body2.copy(
                MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.W500
            ),
            modifier = Modifier
                .constrainAs(tvYes) {
                    top.linkTo(tvSubtitle.bottom, 28.dp)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .clickable {
                    setShowDialog(false)
                    onPositiveButton()
                }
        )
        Text(
            text = stringResource(id = R.string.cancel),
            style = Typography.body2.copy(
                MaterialTheme.colors.onSecondary,
                fontWeight = FontWeight.W500
            ),
            modifier = Modifier
                .constrainAs(tvCancel) {
                    top.linkTo(tvSubtitle.bottom, 28.dp)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(tvYes.start, 38.dp)
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
    WorxTheme() {
        DetailFormScreen(
            viewModel = viewModel,
            cameraViewModel,
            scannerViewModel,
            Session(LocalContext.current),
            {}
        )
    }
}