package id.worx.device.client.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.worx.device.client.R
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.screen.FormScreen
import id.worx.device.client.screen.RedFullWidthButton
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModel

sealed class BottomNavItem(var title: Int, var icon: Int, var screen_route: String) {

    object Form : BottomNavItem(R.string.form, R.drawable.ic_form, "form")
    object Draft : BottomNavItem(R.string.draft, R.drawable.ic_draft, "draft")
    object Submission : BottomNavItem(R.string.submission, R.drawable.ic_tick, "submission")
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    formList: List<EmptyForm>,
    draftList: List<SubmitForm>,
    submissionList: List<SubmitForm>,
    viewModel: HomeViewModel,
    detailVM: DetailFormViewModel
) {
    NavHost(navController, startDestination = BottomNavItem.Form.screen_route) {
        composable(BottomNavItem.Form.screen_route) {
            FormScreen(
                formList,
                viewModel,
                detailVM,
                stringResource(R.string.no_forms),
                stringResource(R.string.empty_description_form)
            )
        }
        composable(BottomNavItem.Draft.screen_route) {
            FormScreen(
                draftList,
                viewModel,
                detailVM,
                stringResource(R.string.no_drafts),
                stringResource(R.string.empty_description_drafts)
            )
        }
        composable(BottomNavItem.Submission.screen_route) {
            FormScreen(
                submissionList,
                viewModel,
                detailVM,
                stringResource(R.string.no_submission),
                stringResource(R.string.empty_description_submission)
            )
        }
    }
}

@Composable
fun HomeScreen(
    formList: List<EmptyForm>,
    draftList: List<SubmitForm>,
    submissionList: List<SubmitForm>,
    viewModel: HomeViewModel,
    detailVM: DetailFormViewModel
) {
    val navController = rememberNavController()
    val notificationType = viewModel.showNotification.collectAsState().value
    var showSubmittedStatus by remember { mutableStateOf(notificationType == 1) }

    Scaffold(
        topBar = {
            MainTopAppBar(
                navController,
                viewModel,
                detailVM
            )
        },
        bottomBar = {
            BottomNavigationView(navController = navController)
        }
    ) { padding ->
        NavigationGraph(
            navController = navController,
            formList = formList,
            draftList = draftList,
            submissionList = submissionList,
            viewModel = viewModel,
            detailVM = detailVM
        )
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
            FormSubmitted {
                viewModel.showNotification(0)
                showSubmittedStatus = false
            }
        }
    }
}

@Composable
fun BottomNavigationView(navController: NavController) {
    val items = listOf(
        BottomNavItem.Form,
        BottomNavItem.Draft,
        BottomNavItem.Submission,
    )
    BottomNavigation(
        backgroundColor = Color.White,
        modifier = Modifier.border(1.5.dp, Color.Black)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            var modifierBorder = Modifier.border(0.dp, Color.Black)
            if (item.title == R.string.draft) modifierBorder = Modifier.border(1.5.dp, Color.Black)

            BottomNavigationItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = stringResource(id = item.title)
                    )
                },
                label = {
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = stringResource(id = item.title),
                        fontSize = 11.sp, fontFamily = FontFamily.Monospace,
                        color = if (currentRoute == item.screen_route) {
                            Color.White
                        } else {
                            Color.Black.copy(0.3f)
                        }
                    )
                },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.Black.copy(alpha = 0.3f),
                selected = currentRoute == item.screen_route,
                onClick = {
                    navController.navigate(item.screen_route) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = if (currentRoute == item.screen_route) {
                    modifierBorder.background(PrimaryMain)
                } else {
                    modifierBorder.background(color = Color.White)
                },
            )
        }
    }
}

@Composable
fun MainTopAppBar(
    navController: NavController,
    viewModel: HomeViewModel,
    detailVM: DetailFormViewModel
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PrimaryMain,
        contentColor = Color.White
    ) {
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
                text = "PT Virtue Digital Indonesia",
                style = Typography.body1
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
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

@Preview(showSystemUi = true)
@Composable
private fun BottomNavPreview(
    viewModel: HomeViewModel = hiltViewModel(),
    detailVM: DetailFormViewModel = hiltViewModel()
) {
    val list = arrayListOf<EmptyForm>()
    HomeScreen(list, arrayListOf(), arrayListOf(), viewModel, detailVM)
}
