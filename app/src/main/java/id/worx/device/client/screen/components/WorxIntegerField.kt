package id.worx.device.client.screen.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.worx.device.client.model.IntegerValue
import id.worx.device.client.theme.LocalWorxColorsPalette
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.fontRoboto
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxIntegerField(
    index: Int,
    viewModel:DetailFormViewModel,
    validation: Boolean = false,
) {
    val form = viewModel.uiState.collectAsState().value.detailForm!!.fields.getOrNull(index)
    val formStatus = viewModel.uiState.collectAsState().value.status
    val integerValue = viewModel.uiState.collectAsState().value.values[form?.id] as IntegerValue?
    val value = if (integerValue != null) {
        remember { mutableStateOf<String?>(integerValue.value.toString()) }
    } else {
        remember { mutableStateOf<String?>(null) }
    }

    val warningInfo = if (form?.required == true && value.value == null) "${form.label} is required" else ""

    WorxBaseField(
        indexForm = index,
        viewModel = viewModel,
        validation = validation,
        warningInfo = warningInfo
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = !arrayListOf(EventStatus.Done, EventStatus.Submitted).contains(formStatus),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = LocalWorxColorsPalette.current.homeBackground,
                unfocusedIndicatorColor = LocalWorxColorsPalette.current.textFieldUnfocusedIndicator,
                focusedIndicatorColor = LocalWorxColorsPalette.current.textFieldFocusedIndicator
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = if (value.value.isNullOrEmpty()) {
                Typography.body2.copy(color = MaterialTheme.colors.onSecondary.copy(0.54f))
            } else {
                Typography.body2.copy(MaterialTheme.colors.onSecondary)
            },
            shape = RoundedCornerShape(4.dp),
            value = value.value ?: "",
            onValueChange = {
                value.value = it
                if (value.value.isNullOrEmpty()){
                    viewModel.setComponentData(index, null)
                }
                else  {
                    try {
                        val int = Integer.parseInt( value.value!!)
                        viewModel.setComponentData(index, IntegerValue(value = int))
                    } catch (_: java.lang.NumberFormatException){}
                }
            },
            label = {
                Text(
                    text = "Input Integer Number",
                    fontFamily = fontRoboto,
                    style = Typography.body2,
                    color = MaterialTheme.colors.onSecondary.copy(0.54f)
                )
            })
    }
}