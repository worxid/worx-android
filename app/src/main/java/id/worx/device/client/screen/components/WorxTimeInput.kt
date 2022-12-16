package id.worx.device.client.screen.components

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
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
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.TimeField
import id.worx.device.client.model.TimeValue
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus


@Composable
fun WorxTimeInput(indexForm: Int, viewModel: DetailFormViewModel, session: Session, validation : Boolean = false) {
    val theme = session.theme
    val form = viewModel.uiState.collectAsState().value.detailForm?.fields?.get(indexForm)!! as TimeField
    val formStatus = viewModel.uiState.collectAsState().value.status
    val timeValue = viewModel.uiState.collectAsState().value.values[form.id] as TimeValue?
    val value = if (timeValue != null) {
        remember { mutableStateOf(timeValue.value) }
    } else {
        remember { mutableStateOf<id.worx.device.client.model.LocalTime?>(null) }
    }
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val warningInfo = if (form.required == true && value.value == null) "${form.label} is required" else ""

    val timePickerCallback = TimePickerDialog.OnTimeSetListener { timePicker, hr, min ->
        value.value = id.worx.device.client.model.LocalTime(hour = hr, minute = min)
        viewModel.setComponentData(indexForm, TimeValue(value = value.value))
        showTimePicker = false
    }

    val style = when(theme){
        SettingTheme.Blue -> R.style.BlueCalenderViewCustom
        SettingTheme.Green -> R.style.GreenCalenderViewCustom
        else -> R.style.CalenderViewCustom
    }
    val mTimeSliderDialog = WorxTimeSliderDialog(
        context,
        style,
        value.value?.hour ?: 0, value.value?.minute ?: 0
    )

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        Text(
            form.label ?: "",
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (!form.description.isNullOrBlank()) {
            Text(
                text = form.description!!,
                color = if (theme == SettingTheme.Dark) textFormDescriptionDark else textFormDescription,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier.padding(end = 12.dp)
                    .weight(1f),
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Black.copy(0.06f)
                ),
                textStyle = if (value.value == null) {
                    Typography.body2.copy(color = MaterialTheme.colors.onSecondary.copy(0.54f))
                } else {
                    Typography.body2.copy(MaterialTheme.colors.onSecondary)
                },
                shape = RoundedCornerShape(4.dp),
                value = if (value.value == null) {
                    "Answer"
                } else {
                    String.format("%02d:%02d", value.value!!.hour, value.value!!.minute)
                },
                onValueChange = {})
            Box(modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (theme == SettingTheme.Dark) Color.White else MaterialTheme.colors.background.copy(
                        0.10f
                    )
                )
                .clickable { showTimePicker = true }
                .height(TextFieldDefaults.MinHeight)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = "Time Picker",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }
        if (warningInfo.isNotBlank()) {
            if (validation){
                Text(
                    text = warningInfo,
                    modifier = Modifier
                         .padding(top = 4.dp),
                    color = PrimaryMain
                )
            }
            form.isValid = false
        } else {
            form.isValid = true
        }
        if (showTimePicker && !arrayListOf(
                EventStatus.Done,
                EventStatus.Submitted
            ).contains(formStatus)) {
            mTimeSliderDialog.setOnCancelListener { showTimePicker = false }
            mTimeSliderDialog.showDialog()
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(vertical = 16.dp))
    }
}

class WorxTimeSliderDialog(context: Context, style:Int, hr: Int, min: Int) : AlertDialog(context) {
    fun showDialog(){
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.time_picker_dialog)
        val cancel = dialog.findViewById<TextView>(
            R.id.btn_cancel
        ).apply { setOnClickListener { dialog.dismiss() } }
        dialog.show()
    }
}