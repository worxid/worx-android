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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.TextFieldValue
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.textUnfocusColorDark
import id.worx.device.client.theme.textUnfocusColorSystem
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxIntegerField(
    index: Int,
    viewModel:DetailFormViewModel,
    session: Session,
    validation: Boolean = false,
) {
    val form = viewModel.uiState.collectAsState().value.detailForm!!.fields.getOrNull(index)
    val formStatus = viewModel.uiState.collectAsState().value.status
    val integerValue = viewModel.uiState.collectAsState().value.values[form?.id] as TextFieldValue?
    val value = if (integerValue != null) {
        remember { mutableStateOf(integerValue.values) }
    } else {
        remember { mutableStateOf<String?>(null) }
    }

    val warningInfo = if (form?.required == true && value.value == null) "${form.label} is required" else ""

    WorxBaseField(
        indexForm = index,
        viewModel = viewModel,
        validation = validation,
        session = session,
        warningInfo = warningInfo
    ) {
        TextField(
            modifier = Modifier.padding(horizontal = 16.dp)
                .fillMaxWidth(),
            enabled = !arrayListOf(EventStatus.Done, EventStatus.Submitted).contains(formStatus),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Black.copy(0.06f)
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
                viewModel.setComponentData(index, integerValue)
            },
            label = {
                Text(
                    text = "Input Integer Number",
                    fontFamily = FontFamily.Monospace,
                    color = if (session.theme == SettingTheme.Dark) textUnfocusColorDark else textUnfocusColorSystem
                )
            })
    }
}