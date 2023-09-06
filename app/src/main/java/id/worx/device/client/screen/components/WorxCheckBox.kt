package id.worx.device.client.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.fieldmodel.CheckBoxField
import id.worx.device.client.model.fieldmodel.CheckBoxValue
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxCustomColorsPalette
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxCheckBox(
    indexForm: Int,
    viewModel: DetailFormViewModel,
    validation: Boolean = false,
    session: Session
) {
    val form =
        viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as CheckBoxField
    val formStatus = viewModel.uiState.collectAsState().value.status
    val title = form.label ?: ""
    val optionTitles = form.group
    val minChecked = form.minChecked ?: 0

    val checkBoxValue = viewModel.uiState.collectAsState().value.values[form.id] as CheckBoxValue?
    val value = if (checkBoxValue != null) {
        remember { mutableStateOf(checkBoxValue.value.toList()) }
    } else {
        remember {
            mutableStateOf(optionTitles.map { false })
        }
    }
    val totalCheckOptions = remember {
        mutableStateOf(value.value.count { it })
    }
    val warningInfo = if (form.required == true && totalCheckOptions.value == 0)
        "$title is required"
    else if (minChecked > totalCheckOptions.value) "Select minimum $minChecked options" else ""

    WorxBaseField(
        indexForm = indexForm,
        viewModel = viewModel,
        validation = validation,
        session = session,
        warningInfo = warningInfo
    ) {
        Column {
            optionTitles.forEachIndexed() { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (!arrayListOf(
                                EventStatus.Done,
                                EventStatus.Submitted
                            ).contains(formStatus)
                        ) {
                            value.value = value.value.mapIndexed { indexlist, b ->
                                if (index == indexlist) {
                                    !b
                                } else {
                                    b
                                }
                            }
                            viewModel.setComponentData(
                                indexForm,
                                CheckBoxValue(value = ArrayList(value.value))
                            )
                            totalCheckOptions.value = value.value.count { it }
                        }
                    }
                ) {
                    Checkbox(
                        checked = value.value[index],
                        onCheckedChange = {
                            if (!arrayListOf(
                                    EventStatus.Done,
                                    EventStatus.Submitted
                                ).contains(formStatus)
                            ) {
                                value.value = value.value.mapIndexed { indexlist, b ->
                                    if (index == indexlist) {
                                        it
                                    } else {
                                        b
                                    }
                                }
                                viewModel.setComponentData(
                                    indexForm,
                                    CheckBoxValue(value = ArrayList(value.value))
                                )
                                totalCheckOptions.value = value.value.count { it }
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colors.onBackground,
                            checkmarkColor = Color.White,
                            uncheckedColor = WorxCustomColorsPalette.current.optionBorder,
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        item.label ?: "",
                        style = Typography.body1.copy(color = MaterialTheme.colors.onSecondary)
                    )
                }
            }
        }
    }
}