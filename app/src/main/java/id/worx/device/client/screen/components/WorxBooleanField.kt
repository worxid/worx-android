package id.worx.device.client.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.fieldmodel.BooleanField
import id.worx.device.client.model.fieldmodel.BooleanValue
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxBooleanField(
    indexForm: Int,
    viewModel: DetailFormViewModel,
    validation: Boolean = false,
    session: Session
) {
    val form =
        viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as BooleanField
    val formStatus = viewModel.uiState.collectAsState().value.status
    val title = form.label ?: "Yes/No"
    val booleanButtonValue =
        viewModel.uiState.collectAsState().value.values[form.id] as BooleanValue?
    val onCheck = if (booleanButtonValue != null) {
        remember {
            mutableStateOf(booleanButtonValue.value)
        }
    } else {
        remember { mutableStateOf<Boolean?>(null) }
    }
    val warningInfo =
        if (form.required == true && onCheck.value == null) "$title is required" else ""

    WorxBaseField(
        indexForm = indexForm,
        viewModel = viewModel,
        validation = validation,
        session = session,
        warningInfo = warningInfo
    ) {
        Column {
            ConstraintLayout(Modifier.fillMaxWidth().wrapContentHeight()) {
                val (rbYes, rbNo) = createRefs()
                val glVertical = createGuidelineFromStart(0.5f)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.constrainAs(rbYes) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(rbNo.start)
                        width = Dimension.fillToConstraints
                    }) {
                    RadioButton(
                        selected = onCheck.value == true,
                        onClick = {
                            if (!arrayListOf(
                                    EventStatus.Done,
                                    EventStatus.Submitted
                                ).contains(formStatus)
                            ) {
                                onCheck.value = true
                                viewModel.setComponentData(
                                    indexForm,
                                    BooleanValue(value = onCheck.value)
                                )
                            }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colors.onBackground,
                            unselectedColor = MaterialTheme.colors.onSecondary
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        stringResource(id = id.worx.device.client.R.string.yes),
                        style = Typography.body1.copy(MaterialTheme.colors.onSecondary)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.constrainAs(rbNo) {
                        top.linkTo(rbYes.top)
                        bottom.linkTo(rbYes.bottom)
                        end.linkTo(glVertical)
                        width = Dimension.fillToConstraints
                    }) {
                    RadioButton(
                        selected = onCheck.value == false,
                        onClick = {
                            if (!arrayListOf(
                                    EventStatus.Done,
                                    EventStatus.Submitted
                                ).contains(formStatus)
                            ) {
                                onCheck.value = false
                                viewModel.setComponentData(
                                    indexForm,
                                    BooleanValue(value = onCheck.value)
                                )
                            }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colors.onBackground,
                            unselectedColor = MaterialTheme.colors.onSecondary
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        stringResource(id = id.worx.device.client.R.string.no),
                        style = Typography.body1.copy(MaterialTheme.colors.onSecondary)
                    )
                }
            }
            if (!arrayListOf(
                    EventStatus.Done,
                    EventStatus.Submitted
                ).contains(formStatus)
            ) {
                TextButton(
                    onClick = {
                        onCheck.value = null
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Icon",
                        tint = MaterialTheme.colors.onBackground
                    )
                    Text(
                        text = "Reset",
                        style = Typography.body2.copy(MaterialTheme.colors.onBackground),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}