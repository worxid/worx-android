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
import id.worx.device.client.theme.LocalWorxColorsPalette
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.textFormDescription
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxBaseField(
    indexForm:Int,
    viewModel: DetailFormViewModel,
    validation: Boolean,
    warningInfo: String,
    content: @Composable () -> Unit){

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
        if (description.isNotBlank()) {
            Text(
                text = description,
                color = LocalWorxColorsPalette.current.textFormDescription,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 16.dp)
            )
        }
        content()
        if (warningInfo.isNotBlank()) {
            if (validation){
                Text(
                    text = warningInfo,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                    color = PrimaryMain
                )
            }
            form.isValid = false
        } else {
            form.isValid = true
        }
        Divider(color = LocalWorxColorsPalette.current.divider, modifier = Modifier.padding(vertical = 16.dp))
    }
}