package id.worx.device.client.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography

@Composable
fun WorxTextField(
    label: String,
    hint: String? = null,
    inputType: KeyboardOptions,
    initialValue: TextFieldValue = TextFieldValue(),
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    isDeleteTrail: Boolean = false
) {
    var textValue by remember { mutableStateOf(initialValue) }

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