package id.worx.device.client.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.worx.device.client.R
import id.worx.device.client.model.Form
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.HomeViewModel

sealed class BottomNavItem(var title: Int, var icon: Int, var screen_route: String) {

    object Form : BottomNavItem(R.string.form, R.drawable.ic_form, "form")
    object Draft : BottomNavItem(R.string.draft, R.drawable.ic_draft, "draft")
    object Submission : BottomNavItem(R.string.submission, R.drawable.ic_tick, "submission")
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    formList: List<Form>,
    draftList: List<Form>,
    submissionList: List<Form>,
    viewModel: HomeViewModel
) {
    NavHost(navController, startDestination = BottomNavItem.Form.screen_route) {
        composable(BottomNavItem.Form.screen_route) {
            FormScreen(formList, viewModel)
        }
        composable(BottomNavItem.Draft.screen_route) {
            FormScreen(draftList, viewModel)
        }
        composable(BottomNavItem.Submission.screen_route) {
            FormScreen(submissionList, viewModel)
        }
    }
}

@Composable
fun HomeScreen(
    formList: List<Form>,
    draftList: List<Form>,
    submissionList: List<Form>,
    viewModel: HomeViewModel
) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { MainTopAppBar() },
        bottomBar = {
            BottomNavigationView(navController = navController)
        }
    ) { padding ->
        NavigationGraph(
            navController = navController,
            formList = formList,
            draftList = draftList,
            submissionList = submissionList,
            viewModel = viewModel
        )
        AnimatedVisibility(
            visible = viewModel.uiState.collectAsState().value.isLoading
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
                        fontSize = 11.sp, fontFamily = FontFamily.Monospace
                    )
                },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.Black.copy(alpha = 0.3f),
                alwaysShowLabel = true,
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
fun MainTopAppBar() {
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
                modifier = Modifier.padding(horizontal = 20.dp),
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings"
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun BottomNavPreview(viewModel: HomeViewModel = hiltViewModel()) {
    val list = arrayListOf<Form>()
    HomeScreen(list, list, list, viewModel)
}
