package id.worx.device.client.screen.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorxDateInput(indexForm:Int, viewModel: DetailFormViewModel) {
    val selectedDate = viewModel.uiState.detailForm?.componentList?.get(indexForm)?.Outputdata ?: ""
    var showDatePicker by remember { mutableStateOf(false) }

    val c = Calendar.getInstance()
    var year = c.get(Calendar.YEAR)
    var month = c.get(Calendar.MONTH)
    var day = c.get(Calendar.DAY_OF_MONTH)

    if (selectedDate.isNotEmpty() || selectedDate != ""){
        val dateVM : Date = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).parse(selectedDate) as Date
        year = dateVM.year
        month = dateVM.month
        day = dateVM.day
    }

    val mDatePickerDialog = DatePickerDialog(
        LocalContext.current, R.style.CalenderViewCustom, { datePicker, yr, mo, date ->
            val newDate = Date(yr, mo, date)
            val dateString = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(newDate)
            viewModel.setComponentData(indexForm, dateString)
            showDatePicker = false
        }, year, month, day
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            viewModel.uiState.detailForm!!.componentList[indexForm].inputData.title,
            style = Typography.body2,
            modifier = Modifier.padding(start = 17.dp, bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TextField(
                modifier = Modifier.padding(end = 12.dp),
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(backgroundColor = GrayDivider),
                textStyle = if (selectedDate.isEmpty()|| selectedDate == "") {
                    Typography.body2.copy(color = Color.Black.copy(0.54f))
                } else {
                    Typography.body2
                },
                shape = RoundedCornerShape(4.dp),
                trailingIcon = {
                    Icon(
                        painterResource(id = R.drawable.ic_delete_circle),
                        contentDescription = "Clear Text",
                        modifier = Modifier
                            .clickable {
                                viewModel.setComponentData(indexForm, "")
                            }
                    )
                },
                value = if (selectedDate != "" || selectedDate.isNotEmpty()) {
                    selectedDate
                } else {
                    "Answer"
                },
                onValueChange = {})
            Box(modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(PrimaryMain.copy(0.10f))
                .clickable { showDatePicker = true }
                .fillMaxSize()
                .height(TextFieldDefaults.MinHeight)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_date_icon),
                    contentDescription = "Date Picker",
                    modifier = Modifier
                        .align(Alignment.Center),
                    tint = PrimaryMain
                )
            }
        }
        if (showDatePicker) {
            mDatePickerDialog.show()
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}