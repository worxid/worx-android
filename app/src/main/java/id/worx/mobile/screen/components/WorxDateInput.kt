package id.worx.mobile.screen.components

import android.app.DatePickerDialog
import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.worx.mobile.R
import id.worx.mobile.data.database.Session
import id.worx.mobile.model.fieldmodel.DateField
import id.worx.mobile.model.fieldmodel.DateValue
import id.worx.mobile.screen.main.isLightMode
import id.worx.mobile.theme.LocalAppTheme
import id.worx.mobile.theme.LocalWorxColorsPalette
import id.worx.mobile.theme.Typography
import id.worx.mobile.viewmodel.DetailFormViewModel
import id.worx.mobile.viewmodel.EventStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WorxDateInput(indexForm: Int, viewModel: DetailFormViewModel, session: Session, validation : Boolean = false) {
    val theme = session.theme
    val form =
        viewModel.uiState.collectAsState().value.detailForm?.fields?.get(indexForm)!! as DateField
    val formStatus = viewModel.uiState.collectAsState().value.status
    val dateValue = viewModel.uiState.collectAsState().value.values[form.id] as DateValue?
    val value = if (dateValue != null) {
        remember { mutableStateOf(dateValue.value) }
    } else {
        remember { mutableStateOf<String?>(null) }
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val warningInfo = if (form.required == true && value.value == null) "${form.label} is required" else ""
    val c = Calendar.getInstance()

    if (value.value != null) {
        c.time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(value.value!!) as Date
    }
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    val datePickerCallback = DatePickerDialog.OnDateSetListener { datePicker, yr, mo, date ->
        val calendar = Calendar.getInstance()
        calendar.set(yr, mo, date)
        val newDate = calendar.time
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(newDate)
        viewModel.setComponentData(indexForm, DateValue(value = dateString))
        value.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(newDate)
        showDatePicker = false
    }

    val style = if (LocalAppTheme.current.isLightMode()) R.style.CalenderViewCustom
    else R.style.CalenderViewCustomDark

    val mDatePickerDialog = WorxDatePickerDialog(
        context,
        style,
        datePickerCallback,
        year,
        month,
        day,
        LocalAppTheme.current.isLightMode()
    )

    WorxBaseField(
        indexForm = indexForm,
        viewModel = viewModel,
        validation = validation,
        warningInfo = warningInfo) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showDatePicker = true },
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = LocalWorxColorsPalette.current.homeBackground,
                    unfocusedIndicatorColor = LocalWorxColorsPalette.current.textFieldUnfocusedIndicator,
                    focusedIndicatorColor = LocalWorxColorsPalette.current.textFieldFocusedIndicator
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
                        Box(
                            modifier = Modifier
                                .clickable { showDatePicker = true }
                                .height(TextFieldDefaults.MinHeight)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_calendar),
                                contentDescription = "Date Picker",
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp),
                                tint = LocalWorxColorsPalette.current.textFieldIcon
                            )
                        }
                    }
                },
                value = if (value.value == null) {
                    "Answer"
                } else {
                    value.value ?: ""
                },
                onValueChange = {}
            )
        }
    }
    if (showDatePicker && !arrayListOf(
            EventStatus.Done,
            EventStatus.Submitted
        ).contains(formStatus)
    ) {
        mDatePickerDialog.setOnCancelListener { showDatePicker = false }
        mDatePickerDialog.show()
    }
}

class WorxDatePickerDialog(
    context: Context,
    themeResId: Int,
    @Nullable listener: OnDateSetListener?,
    year: Int,
    monthOfYear: Int,
    dayOfMonth: Int,
    isLightMode: Boolean
) : DatePickerDialog(context, themeResId, listener, year, monthOfYear, dayOfMonth) {
    init {
        init(context, isLightMode)
    }

    private fun init(context: Context, isLightMode: Boolean) {
        val headerView: ViewGroup? = datePicker.findViewById(
            context.resources.getIdentifier(
                "android:id/date_picker_header",
                "id",
                context.packageName
            )
        )
        headerView?.setBackgroundColor(0xFFFFFF)

        if (isLightMode) {
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
}