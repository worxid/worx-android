package id.worx.device.client.screen.main

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
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.window.Dialog
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.screen.components.RedFullWidthButton
import id.worx.device.client.theme.DarkBackground
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.backgroundFormList
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class BottomNavItem(var title: Int, var icon: Int) {

    object Form : BottomNavItem(R.string.form, R.drawable.ic_form)
    object Draft : BottomNavItem(R.string.draft, R.drawable.ic_draft)
    object Submission : BottomNavItem(R.string.submission, R.drawable.ic_tick)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    formList: List<EmptyForm>,
    draftList: List<SubmitForm>,
    submissionList: List<SubmitForm>,
    viewModel: HomeViewModel,
    detailVM: DetailFormViewModel,
    session: Session,
    syncWithServer: () -> Unit
) {
    val notificationType by viewModel.showNotification.collectAsState()
    val showBadge by viewModel.showBadge.collectAsState()
    var showSubmittedStatus by remember { mutableStateOf(notificationType == 1) }
    var showBotNav by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(0)

    Scaffold(
        topBar = {
            MainTopAppBar(
                title = session.organization ?: "",
                onSearchMode = { showBotNav = it },
                viewModel = viewModel
            ) { input ->
                viewModel.uiState.update {
                    it.copy(searchInput = input)
                }
            }
        },
        bottomBar = {
            BottomNavigationView(
                showBadge = showBadge,
                showBotNav = showBotNav,
                theme = session.theme,
                pagerState = pagerState
            )
        }
    ) { padding ->
        val modifier = Modifier
            .padding(bottom = 56.dp)
            .background(backgroundFormList)
        if (showBotNav) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                count = 3,
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
                        session = session,
                        syncWithServer
                    )
                    1 -> FormScreen(
                        draftList,
                        1,
                        viewModel,
                        detailVM,
                        stringResource(R.string.no_drafts),
                        stringResource(R.string.empty_description_drafts),
                        session = session,
                        syncWithServer
                    )
                    2 -> FormScreen(
                        submissionList,
                        2,
                        viewModel,
                        detailVM,
                        stringResource(R.string.no_submission),
                        stringResource(R.string.empty_description_submission),
                        session = session,
                        syncWithServer
                    )
                }
            }
        } else {
            SearchScreen(
                formList = formList,
                draftList = draftList,
                submissionList = submissionList,
                viewModel = viewModel,
                detailVM = detailVM,
                session = session,
                syncWithServer = syncWithServer,
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
                    .padding(padding)
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        AnimatedVisibility(
            visible = showSubmittedStatus
        )
        {
            FormSubmitted(session = session) {
                viewModel.showNotification(0)
                showSubmittedStatus = false
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomNavigationView(showBadge: Int, showBotNav: Boolean, theme:String?, pagerState: PagerState) {
    val items = listOf(
        BottomNavItem.Form,
        BottomNavItem.Draft,
        BottomNavItem.Submission,
    )
    val scope = rememberCoroutineScope()
    val selectedColor = if (theme == SettingTheme.Dark) PrimaryMain else MaterialTheme.colors.primary
    val unselectedColor = if (theme == SettingTheme.Dark) Color.White else Color.Black.copy(0.64f)
    if (showBotNav) {
        BottomNavigation(
            backgroundColor = if (theme == SettingTheme.Dark) MaterialTheme.colors.secondary else Color.White,
            modifier = Modifier
                .padding(horizontal = 13.5.dp, vertical = 16.dp)
                .border(2.dp, MaterialTheme.colors.onSecondary, RoundedCornerShape(8.dp))
        ) {
            items.forEachIndexed { index, item  ->
                BottomNavigationItem(
                    icon = {
                        BadgedBox(badge = {
                            if (item.title == showBadge) {
                                Badge(
                                    modifier = Modifier.scale(0.7f),
                                    backgroundColor = if (theme == SettingTheme.Dark) PrimaryMain else MaterialTheme.colors.primary
                                )
                            }
                        }) {
                            Box( modifier = Modifier
                                .padding(16.dp)
                                .height(24.dp)
                                ) {
                                Icon(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (index == pagerState.currentPage) selectedColor else unselectedColor),
                                    tint = if (theme == SettingTheme.Dark && index != pagerState.currentPage) DarkBackground.copy(0.64f) else Color.Unspecified,
                                    painter = painterResource(id = item.icon),
                                    contentDescription = stringResource(id = item.title),
                                )
                            }
                        }
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
    onSearchMode: (Boolean) -> Unit,
    viewModel: HomeViewModel,
    searchAction: (String) -> Unit,
) {
    var searchMode by remember { mutableStateOf(false) }
    onSearchMode(!searchMode)

    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.primary,
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
                    contentDescription = "Logo Worx"
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = title,
                    style = Typography.button.copy(Color.White)
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    modifier = Modifier.clickable {
                        searchMode = true
                    },
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
                )
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clickable {
                            viewModel.goToSettingScreen()
                        },
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
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
    viewModel: HomeViewModel
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
    session: Session,
    closeNotification: () -> Unit
) {
    val theme = session.theme
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
                    modifier = Modifier.padding(bottom = 20.dp),
                    theme = theme
                )
            }
        },
        onDismissRequest = {}
    )
}

//@Preview(showSystemUi = true)
//@Composable
//private fun BottomNavPreview(
//    viewModel: HomeViewModel = hiltViewModel(),
//    detailVM: DetailFormViewModel = hiltViewModel(),
//    session: Session = Session(LocalContext.current)
//) {
//    val list = arrayListOf<EmptyForm>()
//    HomeScreen(list, arrayListOf(), arrayListOf(), viewModel, detailVM,session,MainActivity())
//}

@OptIn(ExperimentalPagerApi::class)
@Preview(showBackground = true)
@Composable
private fun BottomNavigationViewPreview(){
    BottomNavigationView(showBadge = 1, showBotNav = true, theme = SettingTheme.System, PagerState(1))
}