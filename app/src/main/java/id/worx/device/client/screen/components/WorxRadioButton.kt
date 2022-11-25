package id.worx.device.client.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.worx.device.client.model.RadioButtonField
import id.worx.device.client.model.RadioButtonValue
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.textFormDescription
import id.worx.device.client.util.VerticalGrid
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxRadiobutton(indexForm: Int, viewModel: DetailFormViewModel, validation: Boolean = false,isValid : (Boolean) -> Unit ={}) {
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
    val warningInfo =if (form.required == true && onCheck.value == null) "$title is required" else ""

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier.padding(start = 16.dp)
        )

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
                            unselectedColor = MaterialTheme.colors.onSecondary
                        )
                    )
                    Text(
                        item.label ?: "",
                        style = Typography.body1.copy(MaterialTheme.colors.onSecondary)
                    )
                }
            }
        }
        if (validation && warningInfo.isNotBlank()) {
            Text(
                text = warningInfo,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                color = PrimaryMain
            )
            isValid(false)
        } else {
            isValid(true)
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}