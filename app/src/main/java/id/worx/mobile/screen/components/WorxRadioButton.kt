package id.worx.mobile.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.worx.mobile.model.fieldmodel.RadioButtonField
import id.worx.mobile.model.fieldmodel.RadioButtonValue
import id.worx.mobile.theme.LocalWorxColorsPalette
import id.worx.mobile.theme.Typography
import id.worx.mobile.util.VerticalGrid
import id.worx.mobile.viewmodel.DetailFormViewModel
import id.worx.mobile.viewmodel.EventStatus

@Composable
fun WorxRadiobutton(indexForm: Int, viewModel: DetailFormViewModel, validation: Boolean = false) {
    val form =
        viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as RadioButtonField
    val formStatus = viewModel.uiState.collectAsState().value.status
    val title = form.label ?: "RadioButton"
    val optionTitles = form.options
    val radioButtonValue =
        viewModel.uiState.collectAsState().value.values[form.id] as RadioButtonValue?
    val onCheck = if (radioButtonValue != null) {
        remember {
            mutableStateOf(radioButtonValue.value)
        }
    } else {
        remember { mutableStateOf<Int?>(null) }
    }
    val warningInfo =
        if (form.required == true && onCheck.value == null) "$title is required" else ""

    WorxBaseField(
        indexForm = indexForm,
        viewModel = viewModel,
        validation = validation,
        warningInfo = warningInfo
    ) {
        Column {
            VerticalGrid {
                optionTitles.forEachIndexed { index, item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = index == onCheck.value,
                            onClick = {
                                if (!arrayListOf(
                                        EventStatus.Done,
                                        EventStatus.Submitted
                                    ).contains(formStatus)
                                ) {
                                    onCheck.value = index
                                    viewModel.setComponentData(
                                        indexForm,
                                        RadioButtonValue(value = onCheck.value)
                                    )
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colors.onBackground,
                                unselectedColor = LocalWorxColorsPalette.current.optionBorder,
                            ),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Text(
                            modifier = Modifier.clickable {
                                if (!arrayListOf(
                                        EventStatus.Done,
                                        EventStatus.Submitted
                                    ).contains(formStatus)
                                ) {
                                    onCheck.value = index
                                    viewModel.setComponentData(
                                        indexForm,
                                        RadioButtonValue(value = onCheck.value)
                                    )
                                }
                            },
                            text = item.label ?: "",
                            style = Typography.body1.copy(MaterialTheme.colors.onSecondary)
                        )
                    }
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