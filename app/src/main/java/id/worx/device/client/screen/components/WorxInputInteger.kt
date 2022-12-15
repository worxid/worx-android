package id.worx.device.client.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.IntegerField
import id.worx.device.client.model.TextFieldValue
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxInputInteger(indexForm: Int, viewModel: DetailFormViewModel, session: Session, validation : Boolean = false) {
    val theme = session.theme
    val form = IntegerField(
        "id", "Input Integer", "Description",
    )
    val formStatus = viewModel.uiState.collectAsState().value.status
    val integerValue = viewModel.uiState.collectAsState().value.values[form.id] as TextFieldValue?
    val value = if (integerValue != null) {
        remember { mutableStateOf(integerValue.values) }
    } else {
        remember { mutableStateOf<String?>(null) }
    }

    val warningInfo = if (form.required == true && value.value == null) "${form.label} is required" else ""

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            form.label ?: "",
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (!form.description.isNullOrBlank()) {
            Text(
                text = form.description!!,
                color = if (theme == SettingTheme.Dark) textFormDescriptionDark else textFormDescription,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
            TextField(
                modifier = Modifier.padding(end = 12.dp),
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
                value = if (value.value == null) {
                    "Input Integer Number"
                } else {
                    value.value ?: ""
                },
                onValueChange = {
                    value.value = it
                    viewModel.setComponentData(indexForm, integerValue)})

        if (warningInfo.isNotBlank()) {
            if (validation){
                Text(
                    text = warningInfo,
                    modifier = Modifier
                        .padding(top = 4.dp),
                    color = PrimaryMain
                )
            }
            form.isValid = false
        } else {
            form.isValid = true
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(vertical = 16.dp))
    }
}