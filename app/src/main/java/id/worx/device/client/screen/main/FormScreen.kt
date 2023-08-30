package id.worx.device.client.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.setValue
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
import id.worx.device.client.model.FormSortType
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.screen.components.WorxBoxPullRefresh
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.fontRoboto
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModelImpl

@Composable
fun FormScreen(
    data: List<BasicForm>?,
    type: Int,
    viewModel: HomeViewModelImpl,
    detailFormViewModel: DetailFormViewModel,
    titleForEmpty: String,
    descriptionForEmpty: String,
    session: Session,
    syncWithServer: () -> Unit
) {
    val searchInput = viewModel.uiState.collectAsState().value.searchInput
    val theme = session.theme
    val title = arrayListOf(R.string.form, R.string.draft, R.string.submission)
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(isNetworkAvailable(context)) }

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
                        .background(PrimaryMain.copy(alpha = 0.16f))
                )
            }
            FormSort(selectedSort = FormSortModel())
            if (data.isNullOrEmpty()) {
                EmptyList(type, titleForEmpty, descriptionForEmpty, session)
            } else {
                LazyColumn(
                    modifier = Modifier.padding(start = 16.dp, bottom = 88.dp, end= 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (type) {
                        0 -> items(items = data, itemContent = { item ->
                            ListItemValidForm(item, viewModel, detailFormViewModel, theme)
                        })
                        1 -> items(items = data, itemContent = { item ->
                            DraftItemForm(item as SubmitForm, viewModel, detailFormViewModel, theme)
                        })
                        2 -> items(items = data, itemContent = { item ->
                            SubmissionItemForm(
                                item as SubmitForm,
                                viewModel,
                                detailFormViewModel,
                                theme
                            )
                        })
                    }
                }
            }
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
            style = Typography.subtitle2.copy(
                fontFamily = fontRoboto,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryMain,
                fontSize = 14.sp
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
            style = Typography.caption.copy(
                fontFamily = fontRoboto,
                fontSize = 12.sp,
                color = MaterialTheme.colors.onSecondary
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
fun FormSort(selectedSort: FormSortModel) {
    ConstraintLayout(
        modifier = Modifier
            .padding(16.dp)
    ) {
        val (tvSort, icChevron) = createRefs()
        Text(
            text = selectedSort.formSort.value,
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
            imageVector = if (selectedSort.formSortType == FormSortType.ASC) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
            contentDescription = "Arrow Upward",
            tint = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f),
            modifier = Modifier.size(20.dp).constrainAs(icChevron) {
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
    val bg = arrayListOf(R.drawable.bg_emptylist_form, R.drawable.bg_emptylist_draft, R.drawable.bg_emptylist_submission)
    val icon = arrayListOf(R.drawable.ic_form, R.drawable.ic_draft, R.drawable.ic_tick)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center){
            Image(
                painter = painterResource(id = bg[type]),
                colorFilter = ColorFilter.tint(
                    if (theme == SettingTheme.Dark) PrimaryMain else MaterialTheme.colors.primary
                ) ,
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

