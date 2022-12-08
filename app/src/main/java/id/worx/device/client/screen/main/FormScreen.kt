package id.worx.device.client.screen.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import id.worx.device.client.R
import id.worx.device.client.Util.initProgress
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.BasicForm
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.screen.components.WorxBoxPullRefresh
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.openSans
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.HomeViewModel

@Composable
fun FormScreen(
    data: List<BasicForm>?,
    type: Int,
    viewModel: HomeViewModel,
    detailFormViewModel: DetailFormViewModel,
    titleForEmpty: String,
    descriptionForEmpty: String,
    session: Session,
    syncWithServer: () -> Unit
) {
    val searchInput = viewModel.uiState.collectAsState().value.searchInput
    val theme = session.theme

    WorxBoxPullRefresh(onRefresh = { syncWithServer() }) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.secondary)
        ) {
            val (clNoInternet, content) = createRefs()
            NoConnectionFound(modifier = Modifier
                .background(MaterialTheme.colors.primary.copy(alpha = 0.16f))
                .constrainAs(clNoInternet) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                })
            if (data.isNullOrEmpty()) {
                EmptyList(Modifier.constrainAs(content) {
                    top.linkTo(clNoInternet.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }, titleForEmpty, descriptionForEmpty, session)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .constrainAs(content) {
                            top.linkTo(clNoInternet.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .background(MaterialTheme.colors.secondary)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val (icNoInternet, tvTitle, tvSubtitle) = createRefs()
        Icon(
            painter = painterResource(id = R.drawable.ic_no_internet),
            contentDescription = "Icon No Connection",
            tint = MaterialTheme.colors.primary,
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
                color = MaterialTheme.colors.primary
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
                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.7f)
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
    viewModel: HomeViewModel,
    detailFormViewModel: DetailFormViewModel,
    theme: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, color = MaterialTheme.colors.onSecondary)
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
                fontWeight = FontWeight.W400
            )
            Text(
                text = item.description ?: "",
                style = Typography.body1.copy(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f))
            )
        }
    }
}

@Composable
fun DraftItemForm(
    item: SubmitForm,
    viewModel: HomeViewModel,
    detailFormViewModel: DetailFormViewModel,
    theme: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, color = MaterialTheme.colors.onSecondary)
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
            painter = painterResource(id = if (theme == SettingTheme.Dark) R.drawable.ic_form_white else R.drawable.ic_form_red),
            contentDescription = "Form Icon",
        )
        Column(modifier = Modifier.padding(vertical = 13.dp)) {
            Row() {
                Text(
                    text = "${item.label} - ",
                    style = Typography.button.copy(MaterialTheme.colors.onSecondary),
                )
                Text(
                    text = "Draft",
                    style = Typography.button.copy(PrimaryMain)
                )
            }
            Text(
                text = "Saved on ${item.lastUpdated}",
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
    viewModel: HomeViewModel,
    detailFormViewModel: DetailFormViewModel,
    theme: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, color = MaterialTheme.colors.onSecondary)
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
                style = Typography.button.copy(MaterialTheme.colors.onSecondary)
            )
            Text(
                text = "Submitted on ${item.lastUpdated}",
                style = Typography.body1.copy(color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f))
            )
        }
    }
}

@Composable
fun EmptyList(modifier: Modifier, text: String, description: String, session: Session) {
    val theme = session.theme
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.secondary)
            .verticalScroll(rememberScrollState())
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = if (theme == SettingTheme.Dark) R.drawable.ic_empty_list_icon_dark else R.drawable.ic_empty_list_icon),
            contentDescription = "Empty Icon"
        )
        Text(
            modifier = Modifier.padding(top = 28.dp, bottom = 16.dp),
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

