package id.worx.device.client.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RedFullWidthButton(
    onClickCallback: () -> Unit,
    label: String,
    modifier: Modifier
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = RedDarkButton,
            contentColor = Color.White
        ),
        border = BorderStroke(1.5.dp, Color.Black),
        shape = RoundedCornerShape(1),
        contentPadding = PaddingValues(vertical = 14.dp),
        onClick = onClickCallback
    ) {
        Text(text = label, style = Typography.button)
    }
}

@Composable
fun WorxTopAppBar(
    onBack: () -> Unit,
    progress: Int,
    title: String
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = PrimaryMain,
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = onBack
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back Button")
            }
            Text(
                textAlign = TextAlign.Center,
                text = title,
                style = Typography.h6
            )
            CircularProgressIndicator(
                progress = progress / 100.toFloat(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                color = Color.White.copy(0.3f),
                strokeWidth = 3.dp,
            )
        }
    }
}

@Composable
fun WorxTextField(
    label: String,
    hint: String? = null,
    inputType: KeyboardOptions,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    isDeleteTrail: Boolean = false
) {
    var textValue by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .padding(top = 16.dp)
    ) {
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        Text(
            modifier = Modifier.padding(bottom = 8.dp, start = 17.dp),
            text = label,
            style = Typography.body2
        )
        TextField(
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Black.copy(0.06f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(4.dp),
            value = textValue,
            onValueChange = {
                onValueChange(it.text)
                textValue = it
            },
            label = {
                Text(
                    text = hint ?: "Enter $label",
                    color = Color.Black.copy(0.54f),
                    fontFamily = FontFamily.Monospace
                )
            },
            textStyle = Typography.body1.copy(color = Color.Black),
            keyboardOptions = inputType,
            visualTransformation = if (isPassword && passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.ic_baseline_visibility_24)
                    else painterResource(id = R.drawable.ic_baseline_visibility_off_24)

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = image, description)
                    }
                }
                if (isDeleteTrail) {
                    Icon(
                        painterResource(id = R.drawable.ic_delete_circle),
                        contentDescription = "Clear Text",
                        modifier = Modifier
                            .clickable {
                                textValue = TextFieldValue("")
                            }
                    )
                }
            }
        )
        Divider(
            color = GrayDivider
        )
    }
}

@Composable
fun WorxCheckBox(title: String, optionTitles: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val onCheck = optionTitles.map { component ->
            rememberSaveable { mutableStateOf(false) }
        }.toMutableList()

        Text(title, style = Typography.body2, modifier = Modifier.padding(start = 16.dp))
        Column {
            optionTitles.forEachIndexed() { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = onCheck[index].value,
                        onCheckedChange = {
                            onCheck[index].value = !onCheck[index].value
                        },
                        colors = CheckboxDefaults.colors(PrimaryMain)
                    )
                    Text(item, style = Typography.body1.copy(color = Color.Black))
                }
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun WorxRadiobutton(title: String, optionTitles: List<String>) {
    val onCheck = rememberSaveable { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = Typography.body2, modifier = Modifier.padding(start = 16.dp))
        Column {
            optionTitles.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = onCheck.value == index,
                        onClick = { onCheck.value = index },
                        colors = RadioButtonDefaults.colors(PrimaryMain)
                    )
                    Text(item, style = Typography.body1.copy(Color.Black))
                }
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun WorxDropdown(title: String, optionTitles: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

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
                textStyle = if (selectedIndex == null) {
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
                value = if (selectedIndex != null) {
                    optionTitles[selectedIndex!!]
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
                        selectedIndex = index
                        expanded = false
                    }) {
                        Text(text = item, style = Typography.body1.copy(color = Color.Black))
                    }
                }
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun WorxDateInput(title: String) {
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    val mDatePickerDialog = DatePickerDialog(
        LocalContext.current, R.style.CalenderViewCustom, { datePicker, yr, mo, date ->
            selectedDate = Date(yr, mo, date)
            showDatePicker = false
        }, year, month, day
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
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
                textStyle = if (selectedDate == null) {
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
                                selectedDate = null
                            }
                    )
                },
                value = if (selectedDate != null) {
                    SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(selectedDate!!)
                } else {
                    "Answer"
                },
                onValueChange = {})
            Box(modifier = Modifier
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

@Composable
fun WorxRating(title: String) {
    var rating by remember { mutableStateOf<Int>(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = Typography.body2,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )
        LazyRow(modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(5) { index ->
                Icon(
                    modifier = Modifier.clickable {
                        rating = index+1
                    },
                    painter = painterResource(id = android.R.drawable.star_big_off),
                    contentDescription = "Star Icon",
                    tint = if (index < rating) {
                        SecondaryMain
                    } else {
                        Color(0xFFE0E0E0)
                    }
                )
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
fun WorxAttachFile(title: String) {
    var rating by remember { mutableStateOf<Int>(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = Typography.body2,
            modifier = Modifier.padding(start = 17.dp, bottom = 8.dp, end = 16.dp)
        )
        Row(modifier = Modifier.padding(horizontal = 16.dp)
            .background(PrimaryMain.copy(alpha = 0.1f))
            .clickable { },
        verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(horizontal = 15.dp),
                painter = painterResource(id = android.R.drawable.star_big_off),
                contentDescription = "Attach Icon",
                tint = PrimaryMain
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Add File",
                style = Typography.body2.copy(PrimaryMain),
            )
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}