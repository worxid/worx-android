package id.worx.device.client.screen.components

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.DateField
import id.worx.device.client.model.DateValue
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorxDateInput(indexForm: Int, viewModel: DetailFormViewModel, session: Session) {
    val theme = session.theme
    val form =
        viewModel.uiState.collectAsState().value.detailForm?.fields?.get(indexForm)!! as DateField
    val dateValue = viewModel.uiState.collectAsState().value.values[form.id] as DateValue?
    val value = if (dateValue != null) {
        remember { mutableStateOf(dateValue.value) }
    } else {
        remember { mutableStateOf<String?>(null) }
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val c = Calendar.getInstance()
    var year = c.get(Calendar.YEAR)
    var month = c.get(Calendar.MONTH)
    var day = c.get(Calendar.DAY_OF_MONTH)

    if (value.value != null) {
        val dateVM: Date =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(value.value!!) as Date
        year = dateVM.year
        month = dateVM.month
        day = dateVM.day
    }

    val datePickerCallback = DatePickerDialog.OnDateSetListener { datePicker, yr, mo, date ->
        val calendar = Calendar.getInstance()
        calendar.set(yr, mo, date)
        val newDate = calendar.time
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(newDate)
        viewModel.setComponentData(indexForm, DateValue(value = dateString))
        value.value = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(newDate)
        showDatePicker = false
    }

    val style = when(theme){
        SettingTheme.Blue -> R.style.BlueCalenderViewCustom
        SettingTheme.Green -> R.style.GreenCalenderViewCustom
        else -> R.style.CalenderViewCustom
    }
    val mDatePickerDialog = WorxDatePickerDialog(
        context,
        style,
        datePickerCallback,
        year, month, day
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            form.label ?: "",
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
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
                .clickable { showDatePicker = true }
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
        if (showDatePicker) {
            mDatePickerDialog.show()
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}

class WorxDatePickerDialog : DatePickerDialog {

    @RequiresApi(api = Build.VERSION_CODES.N)
    constructor(context: Context) : super(context) {
        init(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    constructor(context: Context, themeResId: Int) : super(context, themeResId) {
        init(context)
    }

    constructor(
        context: Context,
        @Nullable listener: OnDateSetListener?,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) : super(context, listener, year, month, dayOfMonth) {
        init(context)
    }

    constructor(
        context: Context,
        themeResId: Int,
        @Nullable listener: OnDateSetListener?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int
    ) : super(context, themeResId, listener, year, monthOfYear, dayOfMonth) {
        init(context)
    }

    private fun init(context: Context) {
        val headerView: ViewGroup? = datePicker.findViewById(
            context.resources.getIdentifier(
                "android:id/date_picker_header",
                "id",
                context.packageName
            )
        )
        headerView?.setBackgroundColor(0xFFFFFF)

        val year: TextView? = datePicker.findViewById(
            context.resources.getIdentifier(
                "android:id/date_picker_header_year",
                "id",
                context.packageName
            )
        )
        year?.setTextColor(Color(0x99000000).toArgb())

        val date: TextView? = datePicker.findViewById(
            context.resources.getIdentifier(
                "android:id/date_picker_header_date",
                "id",
                context.packageName
            )
        )
        date?.setTextColor(android.graphics.Color.BLACK)
    }
}