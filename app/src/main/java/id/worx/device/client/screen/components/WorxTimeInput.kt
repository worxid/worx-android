package id.worx.device.client.screen.components

import android.app.TimePickerDialog
import android.content.Context
import androidx.annotation.Nullable
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
    val form = TimeField(time = "19:00")
    val formStatus = viewModel.uiState.collectAsState().value.status
    val timeValue = TimeValue(value = "19:00")
    val value = if (timeValue != null) {
        remember { mutableStateOf(timeValue.value) }
    } else {
        remember { mutableStateOf<String?>(null) }
    }
    var showTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val warningInfo = if (form.required == true && value.value == null) "${form.label} is required" else ""

    val timePickerCallback = TimePickerDialog.OnTimeSetListener { timePicker, hr, min ->
        value.value = "$hr:$min"
        showTimePicker = false
    }

    val style = when(theme){
        SettingTheme.Blue -> R.style.BlueCalenderViewCustom
        SettingTheme.Green -> R.style.GreenCalenderViewCustom
        else -> R.style.CalenderViewCustom
    }
    val mDatePickerDialog = WorxTimePickerDialog(
        context,
        style,
        timePickerCallback,
        0, 0, true
    )

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
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
                modifier = Modifier.padding(end = 12.dp),
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Black.copy(0.06f)
                ),
                textStyle = if (value.value.isNullOrEmpty()) {
                    Typography.body2.copy(color = MaterialTheme.colors.onSecondary.copy(0.54f))
                } else {
                    Typography.body2.copy(MaterialTheme.colors.onSecondary)
                },
                shape = RoundedCornerShape(4.dp),
                trailingIcon = {
                    if (!arrayListOf(
                            EventStatus.Done,
                            EventStatus.Submitted
                        ).contains(formStatus)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_delete_circle),
                            contentDescription = "Clear Text",
                            modifier = Modifier
                                .clickable {
                                    viewModel.setComponentData(indexForm, null)
                                    value.value = null
                                },
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }
                },
                value = if (value.value == null) {
                    "Answer"
                } else {
                    value.value ?: ""
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
                .fillMaxSize()
                .height(TextFieldDefaults.MinHeight)) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_date_icon),
                    contentDescription = "Date Picker",
                    modifier = Modifier
                        .align(Alignment.Center),
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
            mDatePickerDialog.setOnCancelListener { showTimePicker = false }
            mDatePickerDialog.show()
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(vertical = 16.dp))
    }
}

class WorxTimePickerDialog : TimePickerDialog {

    constructor(
        context: Context,
        @Nullable listener: OnTimeSetListener?,
        hr: Int,
        min: Int,
        is24HrView: Boolean
    ) : super(context, listener, hr, min, is24HrView) {
       // init(context)
    }

    constructor(
        context: Context,
        themeResId: Int,
        @Nullable listener: OnTimeSetListener?,
        hr: Int,
        min: Int,
        is24HrView: Boolean
    ) : super(context, themeResId, listener, hr, min, is24HrView) {
        //init(context)
    }

//    private fun init(context: Context) {
//        val headerView: ViewGroup? = timer.findViewById(
//            context.resources.getIdentifier(
//                "android:id/date_picker_header",
//                "id",
//                context.packageName
//            )
//        )
//        headerView?.setBackgroundColor(0xFFFFFF)
//
//        val year: TextView? = datePicker.findViewById(
//            context.resources.getIdentifier(
//                "android:id/date_picker_header_year",
//                "id",
//                context.packageName
//            )
//        )
//        year?.setTextColor(Color(0x99000000).toArgb())
//
//        val date: TextView? = datePicker.findViewById(
//            context.resources.getIdentifier(
//                "android:id/date_picker_header_date",
//                "id",
//                context.packageName
//            )
//        )
//        date?.setTextColor(android.graphics.Color.BLACK)
   // }
}