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
import id.worx.device.client.model.Component
import id.worx.device.client.theme.Typography

/*****************
 *  1 = TextField
 *  2 = Checkbox
 *  3 = Radiobutton
 *  4 = Dropdown
 ******************/
@Composable
fun ValidFormBuilder(componentList: List<Component>) {
   val data = componentList.map { component ->
       remember { mutableStateOf("") }
   }.toMutableList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(items = componentList, itemContent = { index, item ->
            when (item.type) {
                "1" -> {
                    WorxTextField(
                        label = "FreeText",
                        hint = "Answer",
                        inputType = KeyboardOptions(keyboardType = KeyboardType.Text),
                        onValueChange = {
                            data[index].value = it
                        },
                    isDeleteTrail = true)
                }
                "2" -> {
                    WorxCheckBox(
                        title = "Check Box",
                        optionTitles = listOf("Option 1", "Option 2")
                    )
                }
                "3" -> {
                    WorxRadiobutton(
                        title = "Radio Button",
                        optionTitles = listOf("Option 1", "Option 2")
                    )
                }
                "4" -> {
                    WorxDropdown(title = "Dropdown", optionTitles = listOf("Answer 1", "Answer 2", "Answer 3"))
                }
                "5" -> {
                    WorxDateInput(title = "Date")
                }
                "6" -> {
                    WorxRating(title = "Rating")
                }
                "7" -> {
                    WorxAttachFile(title = "File")
                }
                else -> {
                    Text(text = "Unknown component", style = Typography.body1.copy(color = Color.Black))
                }
            }
        })
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewFormComponent(){
    val list = listOf<Component>(
    Component("1",""),
    Component("2",""),
    Component("3",""),
    Component("4",""),
    Component("5",""),
    Component("6",""),
    Component("7",""))

    ValidFormBuilder(componentList = list)
}