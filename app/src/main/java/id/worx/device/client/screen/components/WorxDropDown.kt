package id.worx.device.client.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.device.client.R
import id.worx.device.client.common.applyIf
import id.worx.device.client.model.fieldmodel.DropDownField
import id.worx.device.client.model.fieldmodel.DropDownValue
import id.worx.device.client.theme.DragHandle
import id.worx.device.client.theme.LocalWorxColorsPalette
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxDropdown(
    indexForm: Int,
    viewModel: DetailFormViewModel,
    validation: Boolean = false
) {
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

    WorxBaseField(
        indexForm = indexForm,
        viewModel = viewModel,
        validation = validation,
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
                    .applyIf(
                        !arrayListOf(EventStatus.Done, EventStatus.Submitted).contains(
                            formStatus
                        )
                    ) {
                        clickable {
                            viewModel.updateDropDownVisibility(isShow = true, indexForm)
                        }
                    },
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = LocalWorxColorsPalette.current.homeBackground,
                    unfocusedIndicatorColor = LocalWorxColorsPalette.current.textFieldUnfocusedIndicator,
                    focusedIndicatorColor = LocalWorxColorsPalette.current.textFieldFocusedIndicator
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
                        tint = LocalWorxColorsPalette.current.textFieldIcon
                    )
                },
                value = if (selected.value.value != null) {
                    optionTitles[selected.value.value!!].label ?: ""
                } else {
                    "Answer"
                },
                onValueChange = {}
            )
//            DropdownMenu(
//                expanded =
//                if (!arrayListOf(EventStatus.Done, EventStatus.Submitted).contains(formStatus)
//                ) {
//                    expanded
//                } else {
//                    false
//                },
//                onDismissRequest = { expanded = false },
//                modifier = Modifier
//                    .fillMaxWidth(0.94f)
//                    .background(LocalWorxColorsPalette.current.formItemContainer),
//            ) {
//                optionTitles.forEachIndexed { index, item ->
//                    DropdownMenuItem(
//                        onClick = {
//                            selected.value.value = index
//                            viewModel.setComponentData(indexForm, selected.value)
//                            expanded = false
//                        },
//                        modifier = Modifier.background(LocalWorxColorsPalette.current.formItemContainer)
//                    ) {
//                        Text(
//                            text = item.label ?: "",
//                            style = Typography.body1.copy(
//                                color = LocalWorxColorsPalette.current.textFieldColor
//                            )
//                        )
//                    }
//                }
//            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun WorxNewDropDownBottomSheet(
    indexForm: Int,
    viewModel: DetailFormViewModel
) {
    if (indexForm == -1) return
    var searchInput by remember { mutableStateOf("") }

    val form =
        viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as DropDownField
    val title = form.label ?: "DropDown"
    val optionTitles =
        form.options.filter { (it.label ?: "").contains(other = searchInput, ignoreCase = true) }

    val value = viewModel.uiState.collectAsState().value.values[form.id] as DropDownValue?
    val selected = if (value != null) {
        remember { mutableStateOf(value) }
    } else {
        remember {
            mutableStateOf(DropDownValue())
        }
    }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(
                min = if (optionTitles.isEmpty()) 250.dp else 0.dp,
                max = screenHeight * 0.95f
            )
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        ) {
            Canvas(
                modifier = Modifier.padding(top = 8.dp), onDraw = {
                    drawRect(color = DragHandle)
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cancel",
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                lineHeight = 24.sp,
                color = PrimaryMain,
                modifier = Modifier.clickable {
                    viewModel.updateDropDownVisibility(false)
                }
            )
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = Typography.h6
            )
        }
        BasicTextField(
            value = searchInput,
            onValueChange = {
                searchInput = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                .background(
                    LocalWorxColorsPalette.current.textFieldContainer
                ),
            textStyle = Typography.body1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { /* do something */ }
            ),
            singleLine = true,
            cursorBrush = SolidColor(Color.Black),
            decorationBox = {
                TextFieldDefaults.TextFieldDecorationBox(
                    value = searchInput,
                    innerTextField = it,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = remember { MutableInteractionSource() },
                    placeholder = {
                        Text(
                            "Search",
                            style = Typography.body1.copy(
                                color = MaterialTheme.colors.onSecondary.copy(
                                    alpha = 0.6f
                                )
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "",
                            tint = MaterialTheme.colors.onSecondary.copy(alpha = 0.6f)
                        )
                    },
                    trailingIcon = {
                        if (searchInput.isNotBlank()) {
                            Icon(
                                painterResource(id = R.drawable.ic_delete_circle),
                                contentDescription = "",
                                tint = MaterialTheme.colors.onSecondary.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .clickable {
                                        searchInput = ""
                                    }
                                    .padding(end = 4.dp)
                                    .scale(0.8f)
                            )
                        }
                    },
                    contentPadding = PaddingValues(4.dp)
                )
            }
        )
        if (optionTitles.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Data",
                    style = Typography.body1,
                    color = LocalWorxColorsPalette.current.textFieldColor
                )
            }
        } else {
            LazyColumn {
                itemsIndexed(optionTitles) { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (index == selected.value.value) LocalWorxColorsPalette.current.button.copy(
                                    alpha = 0.08f
                                ) else LocalWorxColorsPalette.current.formItemContainer
                            )
                            .clickable {
                                selected.value.value = index
                                viewModel.setComponentData(indexForm, selected.value)
                                viewModel.updateDropDownVisibility(false)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = index == selected.value.value,
                            onClick = {
                                selected.value.value = index
                                viewModel.setComponentData(indexForm, selected.value)
                                viewModel.updateDropDownVisibility(false)
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = LocalWorxColorsPalette.current.button
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.label ?: "",
                            style = Typography.body2.copy(
                                color = LocalWorxColorsPalette.current.textFieldColor
                            )
                        )
                    }
                }
            }
        }
    }
}