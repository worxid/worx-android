package id.worx.device.client.screen.components

import android.text.BoringLayout
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxTextField(
    theme: String?,
    label: String,
    description: String = "",
    hint: String? = null,
    inputType: KeyboardOptions,
    initialValue: TextFieldValue = TextFieldValue(),
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    isDeleteTrail: Boolean = false,
    isRequired : Boolean = false,
    validation: Boolean = false,
    isEnabled: Boolean = true,
    viewModel:DetailFormViewModel? = null,
    index: Int =-1
) {
    var textValue by remember { mutableStateOf(initialValue) }
    val data = viewModel?.uiState?.collectAsState()?.value?.detailForm?.fields?.getOrNull(index)

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        Text(
            modifier = Modifier.padding(bottom = 8.dp),
            text = label,
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary)
        )
        if (description.isNotBlank()) {
            Text(
                text = description,
                color = if (theme == SettingTheme.Dark) textFormDescriptionDark else textFormDescription,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Black.copy(0.06f),
//                focusedLabelColor = if (theme == SettingTheme.Dark || theme == SettingTheme.System) PrimaryMain else MaterialTheme.colors.primary,
//                unfocusedLabelColor = if (theme == SettingTheme.Dark) textUnfocusColorDark else textUnfocusColorSystem
            ),
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            value = textValue,
            onValueChange = {
                onValueChange(it.text)
                textValue = it
            },
            enabled = isEnabled,
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
                        Icon(
                            painter = image,
                            description,
                            tint = MaterialTheme.colors.onSecondary.copy(0.54f)
                        )
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
        if (isRequired && textValue.text.isEmpty()){
            if (validation){
                Text(
                    text = "$label is required",
                    modifier = Modifier.padding(top = 4.dp),
                    color = PrimaryMain
                )
            }
            data?.isValid = false
        } else {
            data?.isValid = true
        }
        Divider(
            color = GrayDivider,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}