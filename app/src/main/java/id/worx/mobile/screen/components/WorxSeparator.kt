package id.worx.mobile.screen.components

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
import id.worx.mobile.data.database.Session
import id.worx.mobile.model.fieldmodel.Separator
import id.worx.mobile.screen.main.SettingTheme
import id.worx.mobile.theme.Typography
import id.worx.mobile.theme.LocalWorxColorsPalette
import id.worx.mobile.theme.textFormDescription
import id.worx.mobile.theme.textFormDescriptionDark
import id.worx.mobile.viewmodel.DetailFormViewModel

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
            color = LocalWorxColorsPalette.current.divider,
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
            color = LocalWorxColorsPalette.current.divider,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}