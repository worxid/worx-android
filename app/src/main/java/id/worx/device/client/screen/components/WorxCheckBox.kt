package id.worx.device.client.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.worx.device.client.model.CheckBoxField
import id.worx.device.client.model.CheckBoxValue
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxCheckBox(indexForm: Int, viewModel: DetailFormViewModel) {
    val form =
        viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as CheckBoxField
    val formStatus = viewModel.uiState.collectAsState().value.status
    val title = form.label ?: ""
    val optionTitles = form.group

    val checkBoxValue = viewModel.uiState.collectAsState().value.values[form.id] as CheckBoxValue?
    val value = if (checkBoxValue != null) {
        remember { mutableStateOf(checkBoxValue.value.toList()) }
    } else {
        remember {
            mutableStateOf(optionTitles.map { false })
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = Typography.body2.copy(MaterialTheme.colors.onSecondary), modifier = Modifier.padding(start = 16.dp))
        Column {
            optionTitles.forEachIndexed() { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colors.onBackground,
                            checkmarkColor = MaterialTheme.colors.secondary,
                            uncheckedColor = MaterialTheme.colors.onSecondary
                        )
                    )
                    Text(item.label ?: "", style = Typography.body1.copy(color = MaterialTheme.colors.onSecondary))
                }
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}