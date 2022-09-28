package id.worx.device.client.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import id.worx.device.client.R
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

val tabItems = listOf(
    R.string.form to R.drawable.ic_form,
    R.string.draft to R.drawable.ic_draft,
    R.string.submission to R.drawable.ic_tick
)

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    formList: List<EmptyForm>,
    draftList: List<SubmitForm>,
    submissionList: List<SubmitForm>,
    viewModel: HomeViewModel,
    detailVM: DetailFormViewModel
) {
    val searchInput = viewModel.uiState.collectAsState().value.searchInput
    val formData = formList.filter { data -> data.label?.contains(searchInput, true) ?: false}
    val draftData = draftList.filter { data -> data.label?.contains(searchInput, true) ?: false}
    val submissionData = submissionList.filter { data -> data.label?.contains(searchInput, true) ?: false}

    Scaffold() { padding ->
        ConstraintLayout(
            modifier = Modifier.padding(padding)
        ) {
            val (tablayout, tabcontent) = createRefs()
            val pagerState = rememberPagerState(pageCount = 3)

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(tablayout) {
                        top.linkTo(parent.top)
                    },
                backgroundColor = Color.White,
                contentColor = PrimaryMain,
                divider = {
                    Box(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(color = Color.LightGray)
                    )
                }
            ) {
                val scope = rememberCoroutineScope()

                tabItems.forEachIndexed { index, _ ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = stringResource(id = tabItems[index].first),
                                style = Typography.body1,
                                color = if (pagerState.currentPage == index) {
                                    PrimaryMain
                                } else {
                                    Color.Black.copy(0.54f)
                                }
                            )
                        },
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.constrainAs(tabcontent){
                top.linkTo(tablayout.bottom)
            }
                ) { page ->
                when (page) {
                    0 -> FormScreen(
                        formData,
                        0,
                        viewModel,
                        detailVM,
                        stringResource(R.string.no_forms),
                        stringResource(R.string.empty_description_form)
                    )
                    1 -> FormScreen(
                        draftData,
                        1,
                        viewModel,
                        detailVM,
                        stringResource(R.string.no_drafts),
                        stringResource(R.string.empty_description_drafts)
                    )
                    2 -> FormScreen(
                        submissionData,
                        2,
                        viewModel,
                        detailVM,
                        stringResource(R.string.no_submission),
                        stringResource(R.string.empty_description_submission)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSearchScreen() {
    WorxTheme() {
        SearchScreen(
            formList = arrayListOf(),
            draftList = arrayListOf(),
            submissionList = arrayListOf(),
            viewModel = hiltViewModel(),
            detailVM = hiltViewModel()
        )
    }
}