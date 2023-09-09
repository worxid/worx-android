package id.worx.device.client.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import id.worx.device.client.R
import id.worx.device.client.Util.initProgress
import id.worx.device.client.Util.isNetworkAvailable
import id.worx.device.client.model.BasicForm
import id.worx.device.client.model.FormSortModel
import id.worx.device.client.model.FormSortOrderBy
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.screen.components.TransparentButton
import id.worx.device.client.screen.components.WorxBoxPullRefresh
import id.worx.device.client.screen.components.WorxSwipeableCard
import id.worx.device.client.screen.components.WorxTextField
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.PrimaryMainBlue
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxCustomColorsPalette
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
    selectedSort: FormSortModel,
    shouldShowEmptyResult: Boolean = true,
    openSortBottomSheet: () -> Unit,
    syncWithServer: () -> Unit
) {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(isNetworkAvailable(context)) }

    WorxBoxPullRefresh(onRefresh = {
        syncWithServer()
        isConnected = isNetworkAvailable(context)
    }) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(WorxCustomColorsPalette.current.homeBackground),
        ) {
            if (data.isNullOrEmpty()) {
                if (shouldShowEmptyResult) {
                    EmptyList(type, titleForEmpty, descriptionForEmpty)
                }
            } else {
                FormSort(
                    selectedSort = selectedSort,
                    openSortByBottomSheet = openSortBottomSheet
                )
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
                            DraftWrapper(item as SubmitForm, viewModel, detailFormViewModel)
                        })

                        2 -> items(items = data, itemContent = { item ->
                            SubmissionItemForm(item as SubmitForm, viewModel, detailFormViewModel)
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
            .background(WorxCustomColorsPalette.current.iconBackground)
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
            .background(WorxCustomColorsPalette.current.formItemContainer)
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
            tint = WorxCustomColorsPalette.current.iconV2
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DraftWrapper(
    item: SubmitForm,
    viewModel: HomeViewModelImpl,
    detailFormViewModel: DetailFormViewModel
) {
    var showDuplicateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDuplicateDialog) {
        DialogDuplicateDraftForm(
            saveDraft = {
                detailFormViewModel.duplicateDraft(
                    draftForm = item,
                    draftDescription = it
                )
                showDuplicateDialog = false
            }
        ) {
            showDuplicateDialog = false
        }
    }

    if (showDeleteDialog) {
        DialogDeleteDraftForm(
            deleteDraft = {
                detailFormViewModel.deleteDraft(item)
                showDeleteDialog = false
            }
        ) {
            showDeleteDialog = false
        }
    }

    WorxSwipeableCard(
        mainCard = {
            DraftItemForm(
                item = item,
                viewModel = viewModel,
                detailFormViewModel = detailFormViewModel
            )
        },
        leftSwipeCard = {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(PrimaryMain),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.text_delete),
                    tint = MaterialTheme.colors.onPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.text_delete),
                    style = Typography.caption,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        },
        rightSwipeCard = {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(PrimaryMainBlue),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_duplicate),
                    contentDescription = stringResource(R.string.text_duplicate),
                    tint = MaterialTheme.colors.onPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.text_duplicate),
                    style = Typography.caption,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        },
        leftSwiped = {
            showDeleteDialog = true
        },
        rightSwiped = {
            showDuplicateDialog = true
        }
    )
}

@Composable
fun DraftItemForm(
    item: SubmitForm,
    viewModel: HomeViewModelImpl,
    detailFormViewModel: DetailFormViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                .background(WorxCustomColorsPalette.current.formItemContainer)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clickable(
                    onClick = {
                        viewModel.goToDetailScreen()
                        detailFormViewModel.navigateFromHomeScreen(item)
                    }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_form),
                contentDescription = "Form Icon",
                tint = WorxCustomColorsPalette.current.iconV2
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row {
                    Text(
                        text = "${item.label} - ",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = Typography.body1,
                        color = MaterialTheme.colors.onSecondary.copy(0.87f)
                    )
                    Text(
                        text = "Draft",
                        style = Typography.body1,
                        color = WorxCustomColorsPalette.current.iconBackground
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
//                AnimatedVisibility(visible = isExpanded) {
                    Text(
                        text = item.description.toString(),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = Typography.caption,
                        color = MaterialTheme.colors.onSecondary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
//                }
                Text(
                    text = "Saved on ${item.lastUpdated}",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = Typography.caption,
                    color = MaterialTheme.colors.onSecondary.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
//            Icon(
//                imageVector = if (isExpanded) Icons.Outlined.ArrowBackIosNew else Icons.Outlined.KeyboardArrowDown,
//                contentDescription = null,
//                modifier = Modifier.clickable { isExpanded = !isExpanded },
//                tint = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)
//            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
        ) {
            LinearProgressIndicator(
                progress = 1f,
                modifier = Modifier.fillMaxWidth(),
                color = WorxCustomColorsPalette.current.draftLinearProgressIndicator
            )
            LinearProgressIndicator(
                progress = initProgress(item.values.toMutableMap(), item.fields) / 100.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color = WorxCustomColorsPalette.current.draftCompletedLinearProgressIndicator,
                backgroundColor = Color.Transparent
            )
        }
    }
}

@Composable
fun DialogDuplicateDraftForm(
    saveDraft: (draftDescription: String) -> Unit,
    closeDialog: () -> Unit,
) {
    var draftDescription by remember { mutableStateOf("") }
    Dialog(
        content = {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(WorxCustomColorsPalette.current.bottomSheetBackground)
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(R.string.text_duplicate_draft),
                    style = Typography.h6,
                    color = MaterialTheme.colors.onSecondary.copy(0.87f),
                    lineHeight = 24.sp
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(R.string.text_save_draft_desc),
                    style = Typography.body2,
                    color = MaterialTheme.colors.onSecondary.copy(0.87f),
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                WorxTextField(
                    label = "",
                    hint = stringResource(R.string.text_hint_description_optional),
                    inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                    onValueChange = { draftDescription = it },
                    allowMultiline = false
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    TransparentButton(
                        label = "Cancel",
                        modifier = Modifier,
                        onClickCallback = { closeDialog() }
                    )
                    TransparentButton(label = "Save",
                        modifier = Modifier,
                        onClickCallback = {
                            saveDraft(draftDescription.trim())
                        }
                    )
                }
            }
        },
        onDismissRequest = {
            closeDialog()
        }
    )
}

@Composable
fun DialogDeleteDraftForm(
    deleteDraft: () -> Unit,
    closeDialog: () -> Unit,
) {
    Dialog(
        content = {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(WorxCustomColorsPalette.current.bottomSheetBackground)
            ) {
                Text(
                    modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(R.string.text_delete_draft),
                    style = Typography.h6,
                    color = MaterialTheme.colors.onSecondary.copy(0.87f),
                    lineHeight = 24.sp
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp),
                    text = stringResource(R.string.text_are_you_sure_you_want_to_delete_this_form),
                    style = Typography.body2,
                    color = MaterialTheme.colors.onSecondary.copy(0.87f),
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    TransparentButton(
                        label = "Cancel",
                        modifier = Modifier,
                        onClickCallback = { closeDialog() }
                    )
                    TransparentButton(
                        label = "Delete",
                        modifier = Modifier,
                        onClickCallback = {
                            deleteDraft()
                        }
                    )
                }
            }
        },
        onDismissRequest = {
            closeDialog()
        }
    )
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
            .background(WorxCustomColorsPalette.current.formItemContainer)
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
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

