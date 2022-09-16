package id.worx.device.client.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import id.worx.device.client.model.DropDownField
import id.worx.device.client.model.DropDownValue
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxDropdown(indexForm:Int, viewModel: DetailFormViewModel) {
    val form = viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as DropDownField
    val title = form.label ?: "DropDown"
    val optionTitles = form.options

    val value = viewModel.uiState.collectAsState().value.values[form.id] as DropDownValue?
    val selected = if (value != null) {
        remember { mutableStateOf(value) }
    } else {
        remember {
            mutableStateOf(DropDownValue())
        }
    }

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding()
    ) {
        Text(
            title,
            style = Typography.body2,
            modifier = Modifier.padding(start = 17.dp, bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        ) {
            TextField(
                modifier = Modifier
                    .clickable { expanded = true }
                    .fillMaxWidth(),
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = GrayDivider),
                textStyle = if (selected.value.value == null) {
                    Typography.body2.copy(color = Color.Black.copy(0.54f))
                } else {
                    Typography.body2
                },
                shape = RoundedCornerShape(4.dp),
                trailingIcon = {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "DropDown"
                    )
                },
                value = if (selected.value.value != null) {
                    optionTitles[selected.value.value!!].label ?: ""
                } else {
                    "Answer"
                },
                onValueChange = {})
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.94f),
            ) {
                optionTitles.forEachIndexed { index, item ->
                    DropdownMenuItem(onClick = {
                        selected.value.value = index
                        viewModel.setComponentData(indexForm, selected.value)
                        expanded = false
                    }) {
                        Text(text = item.label ?: "", style = Typography.body1.copy(color = Color.Black))
                    }
                }
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}