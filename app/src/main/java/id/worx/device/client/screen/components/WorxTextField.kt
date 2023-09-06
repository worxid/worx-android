package id.worx.device.client.screen.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.WorxCustomColorsPalette
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.fontRoboto
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxTextField(
    label: String,
    hint: String? = null,
    inputType: KeyboardOptions,
    initialValue: TextFieldValue = TextFieldValue(),
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    isDeleteTrail: Boolean = false,
    isRequired: Boolean = false,
    validation: Boolean = false,
    isEnabled: Boolean = true,
    viewModel: DetailFormViewModel? = null,
    index: Int = -1,
    allowMultiline: Boolean = false,
    isShowDivider: Boolean = true,
    horizontalPadding: Dp = 16.dp,
) {
    var textValue by remember { mutableStateOf(initialValue) }
    val data = viewModel?.uiState?.collectAsState()?.value?.detailForm?.fields?.getOrNull(index)

    Log.i("MULTILIN", allowMultiline.toString())

    Column(
        modifier = Modifier.padding(horizontal = horizontalPadding)
    ) {
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        TextField(
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = WorxCustomColorsPalette.current.textFieldContainer,
                focusedLabelColor = WorxCustomColorsPalette.current.textFieldFocusedLabel,
                unfocusedLabelColor = WorxCustomColorsPalette.current.textFieldUnfocusedLabel,
                focusedIndicatorColor = WorxCustomColorsPalette.current.textFieldFocusedIndicator,
                cursorColor = MaterialTheme.colors.onSecondary
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
                    text = label,
                    style = Typography.caption,
                    fontFamily = fontRoboto
                )
            },
            placeholder = {
                Text(
                    text = hint ?: "",
                    style = Typography.body2,
                    fontFamily = fontRoboto,
                    color = MaterialTheme.colors.onSecondary.copy(0.54f)
                )
            },
            textStyle = Typography.body2.copy(color = MaterialTheme.colors.onSecondary),
            keyboardOptions = inputType,
            singleLine = !allowMultiline,
            maxLines = if (allowMultiline) 4 else 1,
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

        if (isShowDivider) {
            Divider(
                color = GrayDivider,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}