package id.worx.device.client.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxRadiobutton(indexForm: Int, viewModel: DetailFormViewModel) {
    val form = viewModel.uiState.detailForm!!.componentList[indexForm]
    val title = form.inputData.title
    val optionTitles = form.inputData.options

    val onCheck = if (form.Outputdata != ""){
        remember{ mutableStateOf<String?>(form.Outputdata)}
    } else {
        remember{ mutableStateOf<String?>(null)}
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = Typography.body2, modifier = Modifier.padding(start = 16.dp))
        Column {
            optionTitles.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = onCheck.value == item,
                        onClick = {
                            onCheck.value = item
                            viewModel.setComponentData(indexForm, item) },
                        colors = RadioButtonDefaults.colors(PrimaryMain)
                    )
                    Text(item, style = Typography.body1.copy(Color.Black))
                }
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}