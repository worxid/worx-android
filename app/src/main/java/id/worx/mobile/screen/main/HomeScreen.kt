package id.worx.mobile.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import id.worx.mobile.R
import id.worx.mobile.data.database.Session
import id.worx.mobile.model.EmptyForm
import id.worx.mobile.model.FormSortModel
import id.worx.mobile.model.SubmitForm
import id.worx.mobile.screen.components.RedFullWidthButton
import id.worx.mobile.screen.components.WorxSortByBottomSheet
import id.worx.mobile.theme.Typography
import id.worx.mobile.theme.LocalWorxColorsPalette
import id.worx.mobile.theme.backgroundFormList
import id.worx.mobile.util.connectivityState
import id.worx.mobile.util.isNoInternet
import id.worx.mobile.viewmodel.DetailFormViewModel
import id.worx.mobile.viewmodel.HomeUiEvent
import id.worx.mobile.viewmodel.HomeViewModelImpl
import id.worx.mobile.viewmodel.ThemeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val SETTINGS_INDEX = 3

sealed class BottomNavItem(var title: Int, var icon: Int, var selectedIcon: Int) {

    object Form :
        BottomNavItem(R.string.form, R.drawable.ic_form_square, R.drawable.ic_form_square_selected)

    object Draft : BottomNavItem(R.string.draft, R.drawable.ic_draft, R.drawable.ic_draft_selected)
    object Submission :
        BottomNavItem(R.string.submission, R.drawable.ic_tick, R.drawable.ic_tick_selected)

    object Setting :
        BottomNavItem(R.string.settings, R.drawable.ic_settings, R.drawable.ic_settings_selected)

}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class,
    ExperimentalCoroutinesApi::class
)
@Composable
fun HomeScreen(
    formList: List<EmptyForm>,
    draftList: List<SubmitForm>,
    submissionList: List<SubmitForm>,
    viewModel: HomeViewModelImpl,
    detailVM: DetailFormViewModel,
    themeViewModel: ThemeViewModel,
    session: Session,
    syncWithServer: () -> Unit,
    selectedSort: FormSortModel
) {
    val notificationType by viewModel.showNotification.collectAsState()
    val showBadge by viewModel.showBadge.collectAsState()
    var showSubmittedStatus by remember { mutableStateOf(notificationType == 1) }
    var showBotNav by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(0)
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val title = arrayListOf(R.string.form, R.string.draft, R.string.submission, R.string.settings)
    val connectionState by connectivityState()

    WorxSortByBottomSheet(
        sheetState = sheetState,
        selectedSort = selectedSort,
        onSortClicked = { viewModel.onEvent(HomeUiEvent.OnSortClicked(it)) }
    ) { openSortBottomSheet ->
        Scaffold(
            topBar = {
                MainTopAppBar(
                    title = stringResource(id = title[pagerState.currentPage]),
                    onSearchMode = { showBotNav = it },
                    viewModel = viewModel,
                    showSearch = pagerState.currentPage != SETTINGS_INDEX
                ) { input ->
                    viewModel.uiState.update {
                        it.copy(searchInput = input)
                    }
                }
            },
            bottomBar = {
                Column {
                    if (connectionState.isNoInternet()) {
                        NoConnectionFound()
                    }
                    Divider(
                        color = LocalWorxColorsPalette.current.bottomNavigationBorder,
                        thickness = 1.5.dp
                    )
                    BottomNavigationView(
                        showBadge = showBadge,
                        showBotNav = showBotNav,
                        pagerState = pagerState
                    )
                }
            }
        ) { padding ->
            val modifier = Modifier
                .padding(padding)
                .background(backgroundFormList)

            if (showBotNav) {
                HorizontalPager(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    count = 4,
                    state = pagerState,
                ) { page ->
                    when (page) {
                        0 -> FormScreen(
                            formList,
                            0,
                            viewModel,
                            detailVM,
                            stringResource(R.string.no_forms),
                            stringResource(R.string.empty_description_form),
                            selectedSort = selectedSort,
                            openSortBottomSheet = openSortBottomSheet,
                            syncWithServer = syncWithServer
                        )

                        1 -> FormScreen(
                            draftList,
                            1,
                            viewModel,
                            detailVM,
                            stringResource(R.string.no_drafts),
                            stringResource(R.string.empty_description_drafts),
                            selectedSort = selectedSort,
                            openSortBottomSheet = openSortBottomSheet,
                            syncWithServer = syncWithServer
                        )

                        2 -> FormScreen(
                            submissionList,
                            2,
                            viewModel,
                            detailVM,
                            stringResource(R.string.no_submission),
                            stringResource(R.string.empty_description_submission),
                            selectedSort = selectedSort,
                            openSortBottomSheet = openSortBottomSheet,
                            syncWithServer = syncWithServer
                        )

                        3 -> {
                            SettingScreen(
                                viewModel,
                                session = session,
                                themeViewModel = themeViewModel
                            )
                        }
                    }
                }
            } else {
                SearchScreen(
                    formList = formList,
                    draftList = draftList,
                    submissionList = submissionList,
                    viewModel = viewModel,
                    detailVM = detailVM,
                    selectedSort = selectedSort,
                    syncWithServer = syncWithServer,
                    openSortBottomSheet = openSortBottomSheet,
                    modifier = modifier
                )
            }

            AnimatedVisibility(
                visible = viewModel.uiState.collectAsState().value.isLoading,
                enter = EnterTransition.None,
                exit = ExitTransition.None
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
            AnimatedVisibility(
                visible = showSubmittedStatus
            )
            {
                FormSubmitted {
                    viewModel.showNotification(0)
                    showSubmittedStatus = false
                }
            }
        }
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavigationView(
    showBadge: Int,
    showBotNav: Boolean,
    pagerState: PagerState
) {
    val items = listOf(
        BottomNavItem.Form,
        BottomNavItem.Draft,
        BottomNavItem.Submission,
        BottomNavItem.Setting
    )
    val colorPalette = LocalWorxColorsPalette.current
    val scope = rememberCoroutineScope()
    if (showBotNav) {
        BottomNavigation(
            backgroundColor = colorPalette.formItemContainer
        ) {
            items.forEachIndexed { index, item ->
                BottomNavigationItem(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .offset(y = 4.dp),
                    icon = {
                        BadgedBox(badge = {
                            if (item.title == showBadge) {
                                Badge(
                                    modifier = Modifier.scale(0.7f),
                                    backgroundColor = colorPalette.button
                                )
                            }
                        }) {
                            Box {
                                Icon(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    tint = Color.Unspecified,
                                    painter = painterResource(id = if (index == pagerState.currentPage) item.selectedIcon else item.icon),
                                    contentDescription = stringResource(id = item.title),
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = stringResource(id = item.title),
                            fontSize = 10.sp,
                            color = if (index == pagerState.currentPage) colorPalette.button else MaterialTheme.colors.onSecondary.copy(
                                alpha = 0.6f
                            ),
                            lineHeight = 11.sp,
                            letterSpacing = 0.15.sp
                        )
                    },
                    selected = index == pagerState.currentPage,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun MainTopAppBar(
    title: String,
    showSearch: Boolean = true,
    onSearchMode: (Boolean) -> Unit,
    viewModel: HomeViewModelImpl,
    searchAction: (String) -> Unit
) {
    var searchMode by remember { mutableStateOf(false) }
    onSearchMode(!searchMode)

    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = LocalWorxColorsPalette.current.appBar,
        contentColor = Color.White
    ) {
        if (!searchMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .height(24.dp)
                        .width(24.dp),
                    painter = painterResource(id = R.drawable.ic_symbol_worx_white),
                    contentDescription = "Logo Worx",
                    tint = Color.White
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = title,
                    style = Typography.h6.copy(Color.White)
                )
                Spacer(modifier = Modifier.weight(1f))
                if (showSearch) {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                searchMode = true
                            }
                            .padding(horizontal = 16.dp),
                        painter = painterResource(id = R.drawable.ic_icon_search),
                        contentDescription = "Search",
                        tint = Color.White
                    )
                }
            }
        } else {
            SearchBar(
                backButton = { searchMode = false },
                inputSearch = searchAction,
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchBar(
    backButton: () -> Unit,
    inputSearch: (String) -> Unit,
    viewModel: HomeViewModelImpl
) {
    val searchData = viewModel.uiState.collectAsState().value.searchInput
    var searchInput by remember { mutableStateOf(TextFieldValue(searchData)) }
    val focusRequest = FocusRequester()
    LaunchedEffect(Unit) {
        focusRequest.requestFocus()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = backButton) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back Button",
                tint = Color.White
            )
        }
        BasicTextField(
            value = searchInput,
            onValueChange = {
                searchInput = it
                inputSearch(it.text)
            },
            modifier = Modifier
                .weight(1f)
                .padding(top = 10.dp, bottom = 10.dp, end = 16.dp)
                .background(
                    Color.White.copy(
                        0.1f
                    ), RoundedCornerShape(4.dp)
                )
                .focusRequester(focusRequest),
            textStyle = Typography.body1,
            cursorBrush = SolidColor(Color.White),
            decorationBox = {
                TextFieldDefaults.TextFieldDecorationBox(
                    value = searchInput.text,
                    innerTextField = it,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    placeholder = {
                        Text(
                            "Search",
                            style = Typography.body1.copy(color = Color.White.copy(0.54f))
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        if (searchInput.text != "") {
                            Icon(
                                painterResource(id = R.drawable.ic_delete_circle),
                                contentDescription = "",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .clickable {
                                        searchInput = TextFieldValue("")
                                    }
                                    .padding(end = 4.dp)
                                    .scale(0.8f)
                            )
                        }
                    },
                    contentPadding = PaddingValues(4.dp)
                )
            })
    }
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
                    .background(MaterialTheme.colors.secondary)
                    .border(1.5.dp, Color.Black),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Image(
                    modifier = Modifier.padding(top = 20.dp),
                    painter = painterResource(id = R.drawable.ic_tick_circle),
                    contentDescription = "Tick"
                )
                Text(
                    text = "Successful submit form",
                    style = Typography.body2.copy(MaterialTheme.colors.onSecondary)
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

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
private fun BottomNavigationViewPreview() {
    BottomNavigationView(
        showBadge = 1,
        showBotNav = true,
        PagerState(1)
    )
}