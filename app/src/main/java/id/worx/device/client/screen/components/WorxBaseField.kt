package id.worx.device.client.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.worx.device.client.data.database.Session
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxBaseField(
    indexForm:Int,
    viewModel: DetailFormViewModel,
    validation: Boolean,
    session: Session,
    warningInfo: String,
    content: @Composable () -> Unit){

    val theme = session.theme
    val form = viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm]
    val title = form.label ?: ""
    val description = form.description ?: ""

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .padding(horizontal = 16.dp)
        )
        Text(
            text = description,
            color = if (theme == SettingTheme.Dark) textFormDescriptionDark else textFormDescription,
            style = MaterialTheme.typography.body1.copy(textFormDescription),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .padding(horizontal = 16.dp)
        )
        content()
        if (warningInfo.isNotBlank()) {
            if (validation){
                Text(
                    text = warningInfo,
                    modifier = Modifier
                        .padding(bottom = 8.dp),
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