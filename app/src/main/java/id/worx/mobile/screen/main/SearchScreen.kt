package id.worx.mobile.screen.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import id.worx.mobile.R
import id.worx.mobile.model.EmptyForm
import id.worx.mobile.model.FormSortModel
import id.worx.mobile.model.SubmitForm
import id.worx.mobile.theme.LocalWorxColorsPalette
import id.worx.mobile.theme.Typography
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.viewmodel.DetailFormViewModel
import id.worx.mobile.viewmodel.HomeViewModelImpl
import kotlinx.coroutines.launch

val tabItems = listOf(
    R.string.form to R.drawable.ic_form,
    R.string.draft to R.drawable.ic_draft,
    R.string.submission to R.drawable.ic_tick
)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchScreen(
    formList: List<EmptyForm>,
    draftList: List<SubmitForm>,
    submissionList: List<SubmitForm>,
    viewModel: HomeViewModelImpl,
    detailVM: DetailFormViewModel,
    selectedSort: FormSortModel,
    syncWithServer: () -> Unit,
    openSortBottomSheet: () -> Unit,
    modifier: Modifier
) {
    val searchInput = viewModel.uiState.collectAsState().value.searchInput
    val formData = formList.filter { data -> data.label?.contains(searchInput, true) ?: false }
    val draftData = draftList.filter { data -> data.label?.contains(searchInput, true) ?: false }
    val submissionData =
        submissionList.filter { data -> data.label?.contains(searchInput, true) ?: false }

    Scaffold { padding ->
        ConstraintLayout(
            modifier = modifier.padding(padding)
        ) {
            val (tablayout, tabcontent) = createRefs()
            val pagerState = rememberPagerState()

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(tablayout) {
                        top.linkTo(parent.top)
                    },
                backgroundColor = LocalWorxColorsPalette.current.appBar,
                contentColor = MaterialTheme.colors.onPrimary,
                divider = {
                    TabRowDefaults.Divider(
                        modifier = Modifier.wrapContentSize(Alignment.BottomStart),
                        thickness = 2.dp,
                        color = LocalWorxColorsPalette.current.appBarDivider
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
                                color = MaterialTheme.colors.onPrimary,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.W400
                            )
                        },
                    )
                }
            }
            HorizontalPager(
                count = 3,
                state = pagerState,
                modifier = Modifier.constrainAs(tabcontent) {
                    top.linkTo(tablayout.bottom)
                }
            ) { page ->
                when (page) {
                    0 -> FormScreen(
                        formData,
                        0,
                        viewModel,
                        detailVM,
                        stringResource(R.string.text_search_empty_title),
                        stringResource(R.string.text_search_empty_desc),
                        selectedSort = selectedSort,
                        shouldShowEmptyResult = searchInput.isNotEmpty(),
                        openSortBottomSheet = openSortBottomSheet,
                        syncWithServer = syncWithServer
                    )

                    1 -> FormScreen(
                        draftData,
                        1,
                        viewModel,
                        detailVM,
                        stringResource(R.string.text_search_empty_title),
                        stringResource(R.string.text_search_empty_desc),
                        selectedSort = selectedSort,
                        shouldShowEmptyResult = searchInput.isNotEmpty(),
                        openSortBottomSheet = openSortBottomSheet,
                        syncWithServer = syncWithServer
                    )

                    2 -> FormScreen(
                        submissionData,
                        2,
                        viewModel,
                        detailVM,
                        stringResource(R.string.text_search_empty_title),
                        stringResource(R.string.text_search_empty_desc),
                        selectedSort = selectedSort,
                        shouldShowEmptyResult = searchInput.isNotEmpty(),
                        openSortBottomSheet = openSortBottomSheet,
                        syncWithServer = syncWithServer
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSearchScreen() {
    WorxTheme {
        SearchScreen(
            formList = arrayListOf(),
            draftList = arrayListOf(),
            submissionList = arrayListOf(),
            viewModel = hiltViewModel(),
            detailVM = hiltViewModel(),
            selectedSort = FormSortModel(),
            openSortBottomSheet = {},
            syncWithServer = {},
            modifier = Modifier
        )
    }
}