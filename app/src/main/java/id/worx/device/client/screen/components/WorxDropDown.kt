package id.worx.device.client.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.fieldmodel.DropDownField
import id.worx.device.client.model.fieldmodel.DropDownValue
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxCustomColorsPalette
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxDropdown(indexForm: Int, viewModel: DetailFormViewModel, session: Session, validation : Boolean = false) {
    val theme = session.theme
    val form =
        viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as DropDownField
    val formStatus = viewModel.uiState.collectAsState().value.status
    val title = form.label ?: "DropDown"
    val optionTitles = form.options

    val value = viewModel.uiState.collectAsState().value.values[form.id] as DropDownValue?
    val selected = if (value != null) {
        remember { mutableStateOf(value) }
    } else {
        remember {
            mutableStateOf(DropDownValue())
        }
    }
    val warningInfo = if (form.required == true && value == null) "$title is required" else ""

    var expanded by remember { mutableStateOf(false) }

    WorxBaseField(
        indexForm = indexForm,
        viewModel = viewModel,
        validation = validation,
        session = session,
        warningInfo = warningInfo
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { expanded = true },
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = WorxCustomColorsPalette.current.homeBackground,
                    unfocusedIndicatorColor = WorxCustomColorsPalette.current.textFieldUnfocusedIndicator,
                    focusedIndicatorColor = WorxCustomColorsPalette.current.textFieldFocusedIndicator
                ),
                textStyle = if (selected.value.value == null) {
                    Typography.body2.copy(color = MaterialTheme.colors.onSecondary.copy(0.54f))
                } else {
                    Typography.body2.copy(color = MaterialTheme.colors.onSecondary)
                },
                shape = RoundedCornerShape(4.dp),
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_circle_arrow_right),
                        contentDescription = "DropDown",
                        tint = WorxCustomColorsPalette.current.textFieldIcon
                    )
                },
                value = if (selected.value.value != null) {
                    optionTitles[selected.value.value!!].label ?: ""
                } else {
                    "Answer"
                },
                onValueChange = {})
            DropdownMenu(
                expanded =
                if (!arrayListOf(EventStatus.Done, EventStatus.Submitted).contains(formStatus)
                ) {
                    expanded
                } else {
                    false
                },
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .background(WorxCustomColorsPalette.current.formItemContainer),
            ) {
                optionTitles.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        onClick = {
                            selected.value.value = index
                            viewModel.setComponentData(indexForm, selected.value)
                            expanded = false
                        },
                        modifier = Modifier.background(WorxCustomColorsPalette.current.formItemContainer)
                    ) {
                        Text(
                            text = item.label ?: "",
                            style = Typography.body1.copy(
                                color = WorxCustomColorsPalette.current.textFieldColor
                            )
                        )
                    }
                }
            }
        }
    }
}