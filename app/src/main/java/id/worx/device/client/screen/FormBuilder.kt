package id.worx.device.client.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.model.Component
import id.worx.device.client.model.InputData
import id.worx.device.client.screen.components.*
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel

/*****************
 *  1 = TextField
 *  2 = Checkbox
 *  3 = Radiobutton
 *  4 = Dropdown
 ******************/
@Composable
fun ValidFormBuilder(
    componentList: List<Component>,
    viewModel: DetailFormViewModel
) {
    val data = componentList.map { component ->
        remember { mutableStateOf("") }
    }.toMutableList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(items = componentList) { index, item ->
            when (item.type) {
                "1" -> {
                    WorxTextField(
                        label = "FreeText",
                        hint = "Answer",
                        inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                        onValueChange = {
                            data[index].value = it
                        },
                        isDeleteTrail = true
                    )
                }
                "2" -> {
                    WorxCheckBox(index, viewModel)
                }
                "3" -> {
                    WorxRadiobutton(index, viewModel)
                }
                "4" -> {
                    WorxDropdown(index, viewModel)
                }
                "5" -> {
                    WorxDateInput(index, viewModel)
                }
                "6" -> {
                    WorxRating(index, viewModel)
                }
                "7" -> {
                    WorxAttachFile(index, viewModel)
                }
                "8" -> {
                    WorxAttachImage(index, viewModel) { viewModel.goToCameraPhoto() }
                }
                else -> {
                    Text(
                        text = "Unknown component",
                        style = Typography.body1.copy(color = Color.Black)
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewFormComponent() {
    val viewModel: DetailFormViewModel = hiltViewModel()
    val list = listOf(
//    Component("1",""),
//    Component("2",""),
//    Component("3",""),
        Component("4", InputData("WorxDropDown")),
        Component("5", InputData("Date")),
        Component("6", InputData("Rating")),
        Component("7", InputData("File")),
        Component("8", InputData("Image"))
    )

    ValidFormBuilder(list, viewModel)
}