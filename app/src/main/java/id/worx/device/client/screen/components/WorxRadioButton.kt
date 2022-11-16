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
import id.worx.device.client.model.RadioButtonField
import id.worx.device.client.model.RadioButtonValue
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxRadiobutton(indexForm: Int, viewModel: DetailFormViewModel) {
    val form = viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as RadioButtonField
    val formStatus = viewModel.uiState.collectAsState().value.status
    val title = form.label ?: "RadioButton"
    val optionTitles = form.options

    val radioButtonValue = viewModel.uiState.collectAsState().value.values[form.id] as RadioButtonValue?
    val onCheck = if (radioButtonValue != null) {
        remember {
            mutableStateOf(radioButtonValue.value)
        }
    } else {
        remember { mutableStateOf<Int?>(null) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = Typography.body2.copy(MaterialTheme.colors.onSecondary), modifier = Modifier.padding(start = 16.dp))
        Column {
            optionTitles.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = index == onCheck.value ,
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
                    Text(item.label ?: "", style = Typography.body1.copy(MaterialTheme.colors.onSecondary))
                }
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}