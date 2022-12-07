package id.worx.device.client.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.device.client.R
import id.worx.device.client.Util.initProgress
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.BasicForm
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.screen.components.WorxBoxPullRefresh
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
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
    val title = arrayListOf(R.string.form, R.string.draft, R.string.submission)

    WorxBoxPullRefresh(onRefresh = { syncWithServer() }) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(MaterialTheme.colors.secondary)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Text(
                text = stringResource(id = title[type]),
                style = Typography.subtitle2.copy(MaterialTheme.colors.onSecondary),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (data.isNullOrEmpty()) {
                EmptyList(type, titleForEmpty, descriptionForEmpty, session)
            } else {
                LazyColumn(
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
fun ListItemValidForm(
    item: BasicForm,
    viewModel: HomeViewModel,
    detailFormViewModel: DetailFormViewModel,
    theme: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color = MaterialTheme.colors.onSecondary, RoundedCornerShape(8.dp))
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
            .border(1.dp, color = MaterialTheme.colors.onSecondary, RoundedCornerShape(8.dp))
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
            .border(1.dp, color = MaterialTheme.colors.onSecondary, RoundedCornerShape(8.dp))
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
fun EmptyList(type: Int, text: String, description: String, session: Session) {
    val theme = session.theme
    val icon = arrayListOf(R.drawable.ic_emptylist_form, R.drawable.ic_emptylist_draft, R.drawable.ic_emptylist_submission)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = icon[type]),
            contentDescription = "Empty Icon"
        )
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

