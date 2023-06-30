package id.worx.device.client.screen.main

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import id.worx.device.client.R
import id.worx.device.client.Util.initProgress
import id.worx.device.client.Util.isNetworkAvailable
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.BasicForm
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.screen.components.WorxBoxPullRefresh
import id.worx.device.client.screen.components.WorxDatePickerDialog
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.PrimaryMainBlue
import id.worx.device.client.theme.PrimaryMainGreen
import id.worx.device.client.theme.RedBackground
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.theme.openSans
import id.worx.device.client.util.ExpandableCard
import id.worx.device.client.util.getReadableLocation
import id.worx.device.client.util.getTimeString
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModelImpl
import id.worx.device.client.viewmodel.PunchStatus
import id.worx.device.client.viewmodel.PunchViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FormScreen(
    data: List<BasicForm>?,
    type: Int,
    viewModel: HomeViewModelImpl,
    detailFormViewModel: DetailFormViewModel,
    punchViewModel: PunchViewModel,
    titleForEmpty: String,
    descriptionForEmpty: String,
    session: Session,
    syncWithServer: () -> Unit,
    onDismissBottomBar: () -> Unit = {},
    onDismissBottomSheet: () -> Unit = {}
) {
    val searchInput = viewModel.uiState.collectAsState().value.searchInput
    val uiState by viewModel.uiState.collectAsState()
    val theme = session.theme
    val title = arrayListOf(R.string.form, R.string.draft, R.string.submission, R.string.summary)
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(isNetworkAvailable(context)) }

    val punchUiState by punchViewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val reportBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = {
            false
        }
    )

    Log.d("Location - Latitude", session.latitude!!.toDouble().toString())
    Log.d("Location - Longitude", session.longitude!!.toDouble().toString())

    val openReportBottomSheet =
        rememberSaveable { mutableStateOf(reportBottomSheetState.isVisible) }

    WorxBoxPullRefresh(onRefresh = {
        syncWithServer()
        isConnected = isNetworkAvailable(context)
    }) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colors.secondary),
        ) {
            if (!isConnected) {
                NoConnectionFound(
                    modifier = Modifier
                        .background(RedBackground)
                )
            }

            if (type == 0) {
                Column {
                    LatestPunchStatus(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .padding(horizontal = 16.dp), username = "Budi",
                        timer = punchUiState.timer,
                        localTime = punchUiState.localTime ?: "01 Jan 1990 00:00:00",
                        punchStatus = punchUiState.punchStatus
                    ) {
                        viewModel.goToPunchScreen()
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    WorkStatus(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        status = uiState.workStatus ?: "Available"
                    ) {
                        viewModel.goToWorkStatusScreen()
                    }
                }
            }

            if (type == 3) {
                AttendanceDate(theme = theme, modifier = Modifier.padding(top = 16.dp))
            }

            Text(
                text = stringResource(id = title[type]),
                style = Typography.subtitle2.copy(MaterialTheme.colors.onSecondary),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
            if (data.isNullOrEmpty()) {
                EmptyList(type, titleForEmpty, descriptionForEmpty, session)
            } else {
                LazyColumn(
                    modifier = Modifier.padding(start = 16.dp, bottom = 88.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (type) {
                        0 -> items(items = data, itemContent = { item ->
                            ListItemValidForm(item, viewModel, detailFormViewModel, theme)
                        })

                        1 -> items(items = data, itemContent = { item ->
                            DraftItemForm(
                                item as SubmitForm,
                                viewModel,
                                detailFormViewModel,
                                theme
                            )
                        })

                        2 -> items(items = data, itemContent = { item ->
                            SubmissionItemForm(
                                item as SubmitForm,
                                viewModel,
                                detailFormViewModel,
                                theme
                            )
                        })

                        3 -> {
                            val items = (1..4).map {
                                it
                            }

                            item {
                                Column {
                                    AttendanceSummary(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        time = punchUiState.timer.getTimeString()
                                    )
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Divider()
                                }
                            }

                            items(items = items) { item ->
                                AttendanceReport(
                                    item = item,
                                    items = items,
                                    theme = theme,
                                    onSubItemClick = {
                                        onDismissBottomBar()
                                        openReportBottomSheet.value = true
                                        coroutineScope.launch {
                                            reportBottomSheetState.show()
                                        }
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
    if (openReportBottomSheet.value && type == 3) {
        ModalBottomSheetLayout(sheetState = reportBottomSheetState, sheetContent = {
            ReportDetail(
                location = getReadableLocation(
                    context,
                    session.latitude?.toDouble(),
                    session.longitude?.toDouble()
                ),
                onCancelButtonClick = {
                    openReportBottomSheet.value = false
                    coroutineScope.launch {
                        reportBottomSheetState.hide()
                    }
                    onDismissBottomSheet()
                },
                onViewImageButtonClick = {
                    viewModel.goToImagePreviewScreen()
                }
            )
        }) {

        }
    }
}

@Composable
fun NoConnectionFound(modifier: Modifier) {
    ConstraintLayout(
        modifier = modifier
            .height(54.dp)
            .fillMaxWidth()
            .padding(horizontal = 17.dp, vertical = 8.dp)
    ) {
        val (icNoInternet, tvTitle, tvSubtitle) = createRefs()
        Icon(
            painter = painterResource(id = R.drawable.ic_no_internet),
            contentDescription = "Icon No Connection",
            tint = PrimaryMain,
            modifier = Modifier.constrainAs(icNoInternet) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
            })
        Text(
            text = stringResource(id = R.string.no_connection),
            style = Typography.body2.copy(
                fontFamily = openSans,
                fontWeight = FontWeight.W600,
                color = PrimaryMain
            ),
            modifier = Modifier.constrainAs(tvTitle) {
                top.linkTo(parent.top)
                start.linkTo(icNoInternet.end, 11.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = stringResource(id = R.string.check_network),
            style = Typography.body1.copy(
                fontFamily = openSans,
                fontSize = 10.sp,
                color = Color.Black.copy(alpha = 0.7f)
            ),
            modifier = Modifier.constrainAs(tvSubtitle) {
                top.linkTo(tvTitle.bottom)
                start.linkTo(tvTitle.start)
                end.linkTo(tvTitle.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
            }
        )
    }
}

@Composable
fun ListItemValidForm(
    item: BasicForm,
    viewModel: HomeViewModelImpl,
    detailFormViewModel: DetailFormViewModel,
    theme: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (theme == SettingTheme.Dark
                ) Color.Unspecified else Color.White
            )
            .border(
                1.dp,
                color = if (theme == SettingTheme.Dark)
                    MaterialTheme.colors.onSecondary
                else MaterialTheme.colors.onSecondary.copy(0.1f),
                RoundedCornerShape(8.dp)
            )
            .clickable(
                onClick = {
                    viewModel.goToDetailScreen()
                    detailFormViewModel.navigateFromHomeScreen(item)
                })
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically),
            painter = painterResource(id = if (theme == SettingTheme.Dark) R.drawable.ic_form_white else R.drawable.ic_form_gray),
            contentDescription = "Form Icon"
        )
        Column(modifier = Modifier.padding(vertical = 13.dp)) {
            Text(
                text = item.label ?: "",
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colors.onSecondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = FontWeight.W400
            )
            Text(
                text = item.description ?: "",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = Typography.body1.copy(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f))
            )
        }
    }
}

@Composable
fun DraftItemForm(
    item: SubmitForm,
    viewModel: HomeViewModelImpl,
    detailFormViewModel: DetailFormViewModel,
    theme: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (theme == SettingTheme.Dark
                ) Color.Unspecified else Color.White
            )
            .border(
                1.dp,
                color = if (theme == SettingTheme.Dark)
                    MaterialTheme.colors.onSecondary
                else MaterialTheme.colors.onSecondary.copy(0.1f),
                RoundedCornerShape(8.dp)
            )
            .clickable(
                onClick = {
                    viewModel.goToDetailScreen()
                    detailFormViewModel.navigateFromHomeScreen(item)
                }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            painter = painterResource(id = if (theme == SettingTheme.Dark) R.drawable.ic_form_white else R.drawable.ic_form_gray),
            contentDescription = "Form Icon",
        )
        Column(modifier = Modifier.padding(vertical = 13.dp)) {
            Row() {
                Text(
                    text = "${item.label} - ",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = Typography.button.copy(MaterialTheme.colors.onSecondary),
                )
                Text(
                    text = "Draft",
                    style = Typography.button.copy(PrimaryMain)
                )
            }
            Text(
                text = "Saved on ${item.lastUpdated}",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = Typography.body1.copy(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f))
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.wrapContentSize()) {
            CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .scale(0.75f),
                color = Color.LightGray,
                strokeWidth = 3.dp,
            )
            CircularProgressIndicator(
                progress = initProgress(item.values.toMutableMap(), item.fields) / 100.toFloat(),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .scale(0.75f),
                color = if (theme == SettingTheme.Dark) MaterialTheme.colors.onSecondary else MaterialTheme.colors.primary,
                strokeWidth = 3.dp,
            )
        }
    }
}

@Composable
fun SubmissionItemForm(
    item: SubmitForm,
    viewModel: HomeViewModelImpl,
    detailFormViewModel: DetailFormViewModel,
    theme: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (theme == SettingTheme.Dark
                ) Color.Unspecified else Color.White
            )
            .border(
                1.dp,
                color = if (theme == SettingTheme.Dark)
                    MaterialTheme.colors.onSecondary
                else MaterialTheme.colors.onSecondary.copy(0.1f),
                RoundedCornerShape(8.dp)
            )
            .clickable(
                onClick = {
                    viewModel.goToDetailScreen()
                    detailFormViewModel.navigateFromHomeScreen(item)
                })
    ) {
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.ic_tick_green),
            contentDescription = "Form Icon"
        )
        Column(modifier = Modifier.padding(vertical = 13.dp)) {
            Text(
                text = item.label ?: "",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = Typography.button.copy(MaterialTheme.colors.onSecondary)
            )
            Text(
                text = "Submitted on ${item.lastUpdated}",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = Typography.body1.copy(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f))
            )
        }
    }
}

@Composable
fun EmptyList(type: Int, text: String, description: String, session: Session) {
    val theme = session.theme
    val bg = arrayListOf(
        R.drawable.bg_emptylist_form,
        R.drawable.bg_emptylist_draft,
        R.drawable.bg_emptylist_submission
    )
    val icon = arrayListOf(R.drawable.ic_form, R.drawable.ic_draft, R.drawable.ic_tick)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = bg[type]),
                colorFilter = ColorFilter.tint(
                    if (theme == SettingTheme.Dark) PrimaryMain else MaterialTheme.colors.primary
                ),
                contentDescription = "Empty Icon"
            )
            Image(
                modifier = Modifier
                    .height(56.dp)
                    .width(56.dp),
                contentScale = ContentScale.Fit,
                painter = painterResource(id = icon[type]),
                contentDescription = "Empty Icon"
            )
        }
        Text(
            modifier = Modifier.padding(top = 28.dp, bottom = 12.dp),
            text = text,
            style = Typography.subtitle1.copy(MaterialTheme.colors.onSecondary)
        )
        Text(
            text = description,
            style = Typography.body2.copy(
                MaterialTheme.colors.onSecondary.copy(0.54f),
                textAlign = TextAlign.Center
            )
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AttendanceReport(
    modifier: Modifier = Modifier,
    item: Int,
    items: List<Int>,
    theme: String?,
    onSubItemClick: () -> Unit = {}
) {
    ExpandableCard(
        title = "Title $item",
        subtitle = "Monday",
        items = items.map { "Sub list $it" },
        theme = theme,
        onSubItemClick = onSubItemClick
    )
}

@Composable
fun AttendanceDate(
    modifier: Modifier = Modifier,
    theme: String?
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val c = Calendar.getInstance()

//    c.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(value.value!!) as Date
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    var currentPicker by remember {
        mutableStateOf<String?>(null)
    }
    val from = remember { mutableStateOf<String?>(null) }
    val to = remember { mutableStateOf<String?>(null) }

    val datePickerCallback = DatePickerDialog.OnDateSetListener { datePicker, yr, mo, date ->
        val calendar = Calendar.getInstance()
        calendar.set(yr, mo, date)
        val newDate = calendar.time
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(newDate)
        if (currentPicker == "from") {
            from.value = dateString
        } else {
            to.value = dateString
        }
        showDatePicker = false
    }

    val style = when (theme) {
        SettingTheme.Blue -> R.style.BlueCalenderViewCustom
        SettingTheme.Green -> R.style.GreenCalenderViewCustom
        else -> R.style.CalenderViewCustom
    }
    val mDatePickerDialog = WorxDatePickerDialog(
        context,
        style,
        datePickerCallback,
        year, month, day
    )

    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(text = "Date", style = Typography.body1.copy(
            color = Color.Black
        ))
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(0.5f)
                    .clickable {
                        currentPicker = "from"
                        showDatePicker = true
                    },
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Black.copy(0.06f)
                ),
                textStyle = if (from.value.isNullOrEmpty()) {
                    Typography.body2.copy(color = MaterialTheme.colors.onSecondary.copy(0.54f))
                } else {
                    Typography.body2.copy(MaterialTheme.colors.onSecondary)
                },
                shape = RoundedCornerShape(4.dp),
                trailingIcon = {
                    if (from.value != null) {
                        Icon(
                            painterResource(id = R.drawable.ic_delete_circle),
                            contentDescription = "Clear Text",
                            modifier = Modifier
                                .clickable {
                                    from.value = null
                                },
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }
                },
                value = if (from.value == null) {
                    "From"
                } else {
                    from.value ?: ""
                },
                onValueChange = {})

            TextField(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .weight(0.5f)
                    .clickable {
                        currentPicker = "to"
                        showDatePicker = true
                    },
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Black.copy(0.06f)
                ),
                textStyle = if (to.value.isNullOrEmpty()) {
                    Typography.body2.copy(color = MaterialTheme.colors.onSecondary.copy(0.54f))
                } else {
                    Typography.body2.copy(MaterialTheme.colors.onSecondary)
                },
                shape = RoundedCornerShape(4.dp),
                trailingIcon = {
                    if (to.value != null) {
                        Icon(
                            painterResource(id = R.drawable.ic_delete_circle),
                            contentDescription = "Clear Text",
                            modifier = Modifier
                                .clickable {
                                    to.value = null
                                },
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }
                },
                value = if (to.value == null) {
                    "To"
                } else {
                    to.value ?: ""
                },
                onValueChange = {})
        }
        Spacer(modifier = Modifier.height(20.dp))
        Divider()
    }
    if (showDatePicker) {
        mDatePickerDialog.setOnCancelListener { showDatePicker = false }
        mDatePickerDialog.show()
    }
}

@Composable
fun AttendanceSummary(
    modifier: Modifier = Modifier,
    time: String
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Present Day", style = Typography.body1.copy(
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "0 Days", style = Typography.body1.copy(
                    color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)
                )
            )
        }
        Column {
            Text(
                text = "Work Hours", style = Typography.body1.copy(
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = time, style = Typography.body1.copy(
                    color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)
                )
            )
        }
        Column {
            Text(
                text = "Absent Day", style = Typography.body1.copy(
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "0 Days", style = Typography.body1.copy(
                    color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LatestPunchStatus(
    modifier: Modifier = Modifier,
    username: String,
    timer: Double,
    localTime: String,
    punchStatus: PunchStatus,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = if (punchStatus == PunchStatus.OUT) PrimaryMainBlue else PrimaryMainGreen,
        contentColor = Color.White,
        border = BorderStroke(1.dp, Color.Black),
        elevation = 6.dp,
        onClick = onClick
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            val (latestPunchText, timeIcon, localTimeText, timelapseIcon, timelapseText, inOutColumn, touchIcon) = createRefs()

            Text(
                modifier = Modifier.constrainAs(latestPunchText) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
                text = "Latest Punch"
            )

            Icon(
                modifier = Modifier
                    .size(12.dp)
                    .constrainAs(timeIcon) {
                        top.linkTo(latestPunchText.bottom, 2.dp)
                        start.linkTo(latestPunchText.start)
                    },
                imageVector = Icons.Default.Schedule,
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .constrainAs(localTimeText) {
                        top.linkTo(timeIcon.top)
                        start.linkTo(timeIcon.end, 8.dp)
                        bottom.linkTo(timeIcon.bottom)
                    },
                text = localTime,
                fontSize = 12.sp
            )

            Icon(
                modifier = Modifier
                    .size(12.dp)
                    .constrainAs(timelapseIcon) {
                        top.linkTo(timeIcon.bottom, 2.dp)
                        start.linkTo(timeIcon.start)
                    },
                imageVector = Icons.Default.Timelapse,
                contentDescription = null
            )

            Text(
                modifier = Modifier.constrainAs(timelapseText) {
                    top.linkTo(timelapseIcon.top)
                    start.linkTo(timelapseIcon.end, 8.dp)
                    bottom.linkTo(timelapseIcon.bottom)
                },
                text = timer.getTimeString(),
                fontSize = 12.sp
            )

            Column(
                modifier = Modifier.constrainAs(inOutColumn) {
                    top.linkTo(parent.top)
                    start.linkTo(localTimeText.end)
                    end.linkTo(touchIcon.start)
                    bottom.linkTo(parent.bottom)
                },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (punchStatus == PunchStatus.OUT) Icons.Default.ArrowBack else Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.Black
                )

                Text(
                    text = if (punchStatus == PunchStatus.OUT) "OUT" else "IN",
                    color = Color.Black
                )
            }

            Icon(
                modifier = Modifier
                    .border(width = 1.dp, color = Color.Black, shape = CircleShape)
                    .background(Color.White, CircleShape)
                    .padding(8.dp)
                    .constrainAs(touchIcon) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                imageVector = Icons.Default.TouchApp,
                contentDescription = null,
                tint = PrimaryMain
            )
        }
    }
}

@Composable
fun ReportDetail(
    modifier: Modifier = Modifier,
    onCancelButtonClick: () -> Unit,
    onViewImageButtonClick: () -> Unit,
    location: String,
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(height = 125.dp, width = 94.dp)
                .background(Color.Gray)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    onViewImageButtonClick()
                    onCancelButtonClick()
                },
            text = "Click on image to view",
            color = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row() {
            Icon(imageVector = Icons.Default.Schedule, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = "Time")
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "08:00",
                    style = Typography.body1.copy(
                        color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row() {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = "Location")
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = location,
                    style = Typography.body1.copy(
                        color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)
                    ),
                    softWrap = true
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = PrimaryMain
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "View on map", color = PrimaryMain)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "CLOCK OUT")
        }

        Spacer(modifier = Modifier.height(36.dp))
        TextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                onCancelButtonClick()
            }) {
            Text(text = "Cancel")
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorkStatus(
    modifier: Modifier = Modifier,
    status: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = Color.White,
        contentColor = Color.White,
        border = BorderStroke(1.dp, Color.Black),
        elevation = 6.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Work Status:", style = Typography.body1.copy(
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = status, style = Typography.body1.copy(
                    color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)
                )
            )
        }
    }
}

@Preview
@Composable
fun LatestPunchStatusPreview() {
    WorxTheme {
        AttendanceDate(theme = "")
    }
}

