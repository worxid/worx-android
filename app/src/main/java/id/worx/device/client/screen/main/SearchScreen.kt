package id.worx.device.client.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import id.worx.device.client.R
import id.worx.device.client.model.EmptyForm
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
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
    submissionList: List<SubmitForm>
) {
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
                                color = PrimaryMain
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun previewSearchScreen() {
    WorxTheme() {
        SearchScreen(
            formList = arrayListOf(),
            draftList = arrayListOf(),
            submissionList = arrayListOf()
        )
    }
}