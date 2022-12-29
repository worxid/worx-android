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
import id.worx.device.client.model.fieldmodel.Separator
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.textFormDescription
import id.worx.device.client.theme.textFormDescriptionDark
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxSeparator(
    indexForm: Int,
    viewModel: DetailFormViewModel,
    session: Session
) {
    val theme = session.theme
    val uiState = viewModel.uiState.collectAsState().value
    val form = uiState.detailForm!!.fields[indexForm] as Separator

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Divider(
            thickness = 6.dp,
            color = GrayDivider,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = form.label ?: "",
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary)
        )
        if (!form.description.isNullOrBlank()) {
            Text(
                text = form.description!!,
                color = if (theme == SettingTheme.Dark) textFormDescriptionDark else textFormDescription,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }


        Divider(
            color = GrayDivider, modifier = Modifier
                .padding(vertical = 16.dp)
        )
    }
}