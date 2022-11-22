package id.worx.device.client.screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.Separator
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.textFormDescription
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxSeparator(
    indexForm: Int,
    description: String,
    viewModel: DetailFormViewModel,
    session: Session
) {
    val theme = session.theme
    val uiState = viewModel.uiState.collectAsState().value
    val form = uiState.detailForm!!.fields[indexForm] as Separator

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp, start = 17.dp),
                text = form.label ?: "",
                style = Typography.body2.copy(MaterialTheme.colors.onSecondary)
            )
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.body1.copy(textFormDescription),
                    modifier = Modifier.padding(bottom = 8.dp, start = 17.dp)
                )
            }


            Divider(
                color = GrayDivider, modifier = Modifier
                    .padding(vertical = 12.dp)
            )
    }
}