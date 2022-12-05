package id.worx.device.client.screen.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import id.worx.device.client.model.CheckBoxField
import id.worx.device.client.model.CheckBoxValue
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.textFormDescription
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxCheckBox(indexForm: Int, viewModel: DetailFormViewModel, validation: Boolean = false,isValid : (Boolean) -> Unit ={}) {
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
    val warningInfo = if (minChecked > totalCheckOptions.value) {
        "Select minimum $minChecked options"
    } else if (form.required == true && totalCheckOptions.value ==0) {
        "$title is required"
    } else {
        ""
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier.padding(bottom = 8.dp).padding(horizontal = 16.dp)
        )
        if (!form.description.isNullOrBlank()) {
            Text(
                text = form.description!!,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier.padding(bottom = 8.dp).padding(horizontal = 16.dp)
            )
        }
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
                                totalCheckOptions.value = value.value.count { it }
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colors.onBackground,
                            checkmarkColor = MaterialTheme.colors.secondary,
                            uncheckedColor = MaterialTheme.colors.onSecondary
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
        if (validation && warningInfo.isNotBlank()) {
            Text(
                text = warningInfo,
                modifier = Modifier
                    .padding(bottom = 8.dp),
                color = PrimaryMain
            )
            isValid(false)
        } else {
            isValid(true)
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(vertical = 16.dp))
    }
}