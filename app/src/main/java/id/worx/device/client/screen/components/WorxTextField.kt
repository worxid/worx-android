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
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.textUnfocusColorDark
import id.worx.device.client.theme.textUnfocusColorSystem

@Composable
fun WorxTextField(
    theme: String?,
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
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary)
        )
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Black.copy(0.06f),
//                focusedLabelColor = if (theme == SettingTheme.Dark || theme == SettingTheme.System) PrimaryMain else MaterialTheme.colors.primary,
//                unfocusedLabelColor = if (theme == SettingTheme.Dark) textUnfocusColorDark else textUnfocusColorSystem
            ),
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
            enabled = isDeleteTrail,
            label = {
                Text(
                    text = hint ?: "Enter $label",
                    fontFamily = FontFamily.Monospace,
                    color = if (theme == SettingTheme.Dark) textUnfocusColorDark else textUnfocusColorSystem
                )
            },
            textStyle = Typography.body1.copy(color = MaterialTheme.colors.onSecondary),
            keyboardOptions = inputType,
            visualTransformation = if (isPassword && passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.ic_baseline_visibility_24)
                    else painterResource(id = R.drawable.ic_baseline_visibility_off_24)

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = image, description, tint = MaterialTheme.colors.onSecondary.copy(0.54f))
                    }
                }
                if (isDeleteTrail) {
                    Icon(
                        painterResource(id = R.drawable.ic_delete_circle),
                        contentDescription = "Clear Text",
                        modifier = Modifier
                            .clickable {
                                textValue = TextFieldValue("")
                            },
                        tint = MaterialTheme.colors.onSecondary
                    )
                }
            }
        )
        Divider(
            color = GrayDivider
        )
    }
}