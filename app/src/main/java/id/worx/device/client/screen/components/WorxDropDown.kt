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
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxDropdown(indexForm:Int, viewModel: DetailFormViewModel) {
    val form = viewModel.uiState.detailForm!!.componentList[indexForm]
    val title = form.inputData.title
    val optionTitles = form.inputData.options

    val selected = remember{ mutableStateOf<String?>(form.Outputdata)}

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
                textStyle = if (selected.value.isNullOrEmpty()) {
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
                value = if (!selected.value.isNullOrEmpty()) {
                    selected.value!!
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
                        selected.value = item
                        viewModel.setComponentData(indexForm, item)
                        expanded = false
                    }) {
                        Text(text = item.toString(), style = Typography.body1.copy(color = Color.Black))
                    }
                }
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}