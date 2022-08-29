package id.worx.device.client.screen

import android.widget.CheckBox
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.device.client.R
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.RedDarkButton
import id.worx.device.client.theme.Typography

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
    inputType: KeyboardOptions,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
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
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            color = Color.Black,
            fontFamily = FontFamily.Monospace
        )
        TextField(
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
                    text = "Enter $label",
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
            }
        )
        Divider(
            color = GrayDivider
        )
    }
}

@Composable
fun WorxCheckBox(title: String, optionTitles: List<String>) {
    Column() {
        Text(title, style = Typography.body1)
        LazyColumn {
            (items(items = optionTitles, itemContent = { item ->
                Row(){
                    Checkbox(
                        checked = false,
                        onCheckedChange = {}
                    )
                    Text(item, style = Typography.body1)
                }
            }))
        }
    }
}

@Composable
fun WorxRadiobutton(title: String, optionTitles: List<String>) {
    Column() {
        Text(title, style = Typography.body1)
        LazyColumn {
            (items(items = optionTitles, itemContent = { item ->
                Row(){
                    RadioButton(
                        selected = false,
                        onClick = { /*TODO*/ })
                    Text(item, style = Typography.body1)
                }
            }))
        }
    }
}