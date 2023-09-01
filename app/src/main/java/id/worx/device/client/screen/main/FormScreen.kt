package id.worx.device.client.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import id.worx.device.client.R
import id.worx.device.client.Util.initProgress
import id.worx.device.client.Util.isNetworkAvailable
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.BasicForm
import id.worx.device.client.model.FormSortModel
import id.worx.device.client.model.FormSortOrderBy
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.screen.components.WorxBoxPullRefresh
import id.worx.device.client.theme.LocalCustomColorsPalette
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.fontRoboto
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModelImpl
import kotlinx.coroutines.launch

@Composable
fun FormScreen(
    data: List<BasicForm>?,
    type: Int,
    viewModel: HomeViewModelImpl,
    detailFormViewModel: DetailFormViewModel,
    titleForEmpty: String,
    descriptionForEmpty: String,
    session: Session,
    selectedSort: FormSortModel? = null,
    openSortBottomSheet: () -> Unit,
    syncWithServer: () -> Unit
) {
    val searchInput = viewModel.uiState.collectAsState().value.searchInput
    val theme = session.theme
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(isNetworkAvailable(context)) }

    WorxBoxPullRefresh(onRefresh = {
        syncWithServer()
        isConnected = isNetworkAvailable(context)
    }) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(LocalCustomColorsPalette.current.homeBackground),
        ) {
            if (data.isNullOrEmpty()) {
                EmptyList(type, titleForEmpty, descriptionForEmpty)
            } else {
                if (selectedSort != null) {
                    FormSort(
                        selectedSort = selectedSort,
                        openSortByBottomSheet = openSortBottomSheet
                    )
                }
                LazyColumn(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    when (type) {
                        0 -> items(items = data, itemContent = { item ->
                            ListItemValidForm(item, viewModel, detailFormViewModel)
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
                                detailFormViewModel
                            )
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun NoConnectionFound(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(LocalCustomColorsPalette.current.iconBackground)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_no_internet),
            contentDescription = "Icon No Connection",
            tint = MaterialTheme.colors.onPrimary,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = stringResource(id = R.string.no_connection),
                style = Typography.subtitle2.copy(
                    fontFamily = fontRoboto,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 14.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(id = R.string.check_network),
                style = Typography.caption.copy(
                    fontFamily = fontRoboto,
                    fontSize = 12.sp,
                    color = MaterialTheme.colors.onPrimary,
                )
            )
        }
    }
}

@Composable
fun FormSort(
    selectedSort: FormSortModel,
    openSortByBottomSheet: suspend () -> Unit
) {
    val scope = rememberCoroutineScope()
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource()
            ) { scope.launch { openSortByBottomSheet() } }
    ) {
        val (tvSort, icChevron) = createRefs()
        Text(
            text = selectedSort.formSortType.value,
            style = Typography.body2.copy(
                fontFamily = fontRoboto,
                fontWeight = FontWeight.W400,
                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f),
                fontSize = 14.sp
            ),
            modifier = Modifier.constrainAs(tvSort) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                width = Dimension.wrapContent
            }
        )
        Icon(
            imageVector = if (selectedSort.formSortOrderBy == FormSortOrderBy.ASC) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
            contentDescription = "Arrow Upward",
            tint = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f),
            modifier = Modifier
                .size(20.dp)
                .constrainAs(icChevron) {
                    top.linkTo(tvSort.top)
                    start.linkTo(tvSort.end, 2.dp)
                    bottom.linkTo(tvSort.bottom)
                    width = Dimension.fillToConstraints
                })
    }
}

@Composable
fun ListItemValidForm(
    item: BasicForm,
    viewModel: HomeViewModelImpl,
    detailFormViewModel: DetailFormViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(LocalCustomColorsPalette.current.formItemContainer)
            .clickable(
                onClick = {
                    viewModel.goToDetailScreen()
                    detailFormViewModel.navigateFromHomeScreen(item)
                }
            )
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.ic_form),
            contentDescription = "Form Icon",
            tint = LocalCustomColorsPalette.current.iconV2
        )
        Column(modifier = Modifier.padding(vertical = 13.dp)) {
            Text(
                text = item.label ?: "",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.87f),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.description ?: "",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.6f),
                fontWeight = FontWeight.W400
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
    detailFormViewModel: DetailFormViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(LocalCustomColorsPalette.current.formItemContainer)
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
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.87f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Submitted on ${item.lastUpdated}",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.6f),
                fontWeight = FontWeight.W400
            )
        }
    }
}

@Composable
fun EmptyList(type: Int, text: String, description: String) {
    val icon = arrayListOf(R.drawable.ic_form_square, R.drawable.ic_draft, R.drawable.ic_tick)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                modifier = Modifier
                    .height(56.dp)
                    .width(56.dp),
                painter = painterResource(id = icon[type]),
                contentDescription = "Empty Icon"
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = text,
            style = Typography.h6.copy(MaterialTheme.colors.onSecondary)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary.copy(0.6f)),
            textAlign = TextAlign.Center
        )
    }
}

