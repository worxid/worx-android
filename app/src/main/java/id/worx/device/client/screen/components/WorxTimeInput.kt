package id.worx.device.client.screen.components

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.TimeField
import id.worx.device.client.model.TimeValue
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
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

    val timePickerCallback = { hr: Int, min: Int ->
        value.value = id.worx.device.client.model.LocalTime(hour = hr, minute = min)
        viewModel.setComponentData(indexForm, TimeValue(value = value.value))
        showTimePicker = false
    }

    val mTimeSliderDialog = WorxTimeSliderDialog(
        context,
        colorTheme = if (theme == SettingTheme.Dark) PrimaryMain else MaterialTheme.colors.primary,
        hr =value.value?.hour ?: 0,
        min = value.value?.minute ?: 0,
        onTimePickerListener = timePickerCallback
    )

    WorxBaseField(
        indexForm = indexForm,
        viewModel = viewModel,
        validation = validation,
        session = session,
        warningInfo = warningInfo
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier
                    .padding(end = 12.dp)
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
    }
        if (showTimePicker && !arrayListOf(
                EventStatus.Done,
                EventStatus.Submitted
            ).contains(formStatus)) {
            mTimeSliderDialog.setOnCancelListener { showTimePicker = false }
            mTimeSliderDialog.showDialog()
        }
}

class WorxTimeSliderDialog(
    context: Context,
    val colorTheme:Color,
    val hr: Int,
    val min: Int,
    val onTimePickerListener: (Int, Int)-> Unit) : AlertDialog(context) {

    fun showDialog(){
        val dialog = Dialog(context)
        dialog.setTitle("Select Time")

        val title = dialog.window?.decorView?.findViewById<TextView>(android.R.id.title)
        title?.textSize = 16f
        title?.typeface = ResourcesCompat.getFont(context, R.font.dmmono)

        dialog.setContentView(R.layout.time_picker_dialog)

        val hrPicker = dialog.findViewById<com.shawnlin.numberpicker.NumberPicker>(R.id.hour_picker)
        hrPicker.setFormatter(R.string.number_picker_formatter)
        hrPicker.value = hr
        hrPicker.selectedTextColor = colorTheme.toArgb()

        val minPicker = dialog.findViewById<com.shawnlin.numberpicker.NumberPicker>(R.id.min_picker)
        minPicker.setFormatter(R.string.number_picker_formatter)
        minPicker.value = min
        minPicker.selectedTextColor = colorTheme.toArgb()

        val cancelButton = dialog.findViewById<TextView>(R.id.btn_cancel)
        cancelButton.setTextColor(colorTheme.toArgb())
        cancelButton.setOnClickListener { dialog.dismiss() }

        val  okButton = dialog.findViewById<Button>(R.id.btn_ok)
        okButton.setBackgroundColor(colorTheme.toArgb())
        okButton.setOnClickListener {
            onTimePickerListener(hrPicker.value, minPicker.value)
            dialog.dismiss()
        }
        dialog.show()
    }
}