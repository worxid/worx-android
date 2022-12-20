package id.worx.device.client.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.RadioButtonField
import id.worx.device.client.model.RadioButtonValue
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxRadiobutton(indexForm: Int, viewModel: DetailFormViewModel, validation: Boolean = false, session: Session) {
    val theme = session.theme
    val form =
        viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as RadioButtonField
    val formStatus = viewModel.uiState.collectAsState().value.status
    val title = form.label ?: "RadioButton"
    val optionTitles = form.options
    val radioButtonValue =
        viewModel.uiState.collectAsState().value.values[form.id] as RadioButtonValue?
    val onCheck = if (radioButtonValue != null) {
        remember {
            mutableStateOf(radioButtonValue.value)
        }
    } else {
        remember { mutableStateOf<Int?>(null) }
    }
    val warningInfo =
        if (form.required == true && onCheck.value == null) "$title is required" else ""

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier
                .padding(bottom = 8.dp)
                .padding(horizontal = 16.dp)
        )
        if (!form.description.isNullOrBlank()) {
            Text(
                text = form.description!!,
                color = if (theme == SettingTheme.Dark) textFormDescriptionDark else textFormDescription,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .padding(horizontal = 16.dp)
            )
        }
        VerticalGrid {
            optionTitles.forEachIndexed { index, item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = index == onCheck.value,
                        onClick = {
                            if (!arrayListOf(
                                    EventStatus.Done,
                                    EventStatus.Submitted
                                ).contains(formStatus)
                            ) {
                                onCheck.value = index
                                viewModel.setComponentData(
                                    indexForm,
                                    RadioButtonValue(value = onCheck.value)
                                )
                            }
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colors.onBackground,
                            unselectedColor = MaterialTheme.colors.onSecondary
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Text(
                        item.label ?: "",
                        style = Typography.body1.copy(MaterialTheme.colors.onSecondary)
                    )
                }
            }
        }
        if (!arrayListOf(
                EventStatus.Done,
                EventStatus.Submitted
            ).contains(formStatus)
        ) {
            TextButton(
                onClick = {
                    onCheck.value = null
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Icon",
                    tint = MaterialTheme.colors.onBackground
                )
                Text(
                    text = "Reset",
                    style = Typography.body2.copy(MaterialTheme.colors.onBackground),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        if (warningInfo.isNotBlank()) {
            if (validation) {
                Text(
                    text = warningInfo,
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    color = PrimaryMain
                )
            }
            form.isValid = false
        } else {
            form.isValid = true
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(vertical = 16.dp))
    }
}

/**
 * A simple grid which lays elements out vertically in evenly sized [columns].
 */
@Composable
private fun VerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val itemWidth = constraints.maxWidth / columns
        // Keep given height constraints, but set an exact width
        val itemConstraints = constraints.copy(
            minWidth = itemWidth,
            maxWidth = itemWidth
        )
        // Measure each item with these constraints
        val placeables = measurables.map { it.measure(itemConstraints) }
        // Track each columns height so we can calculate the overall height
        val columnHeights = Array(columns) { 0 }
        placeables.forEachIndexed { index, placeable ->
            val column = index % columns
            columnHeights[column] += placeable.height
        }
        val height = (columnHeights.maxOrNull() ?: constraints.minHeight)
            .coerceAtMost(constraints.maxHeight)
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            // Track the Y co-ord per column we have placed up to
            val columnY = Array(columns) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val column = index % columns
                placeable.placeRelative(
                    x = column * itemWidth,
                    y = columnY[column]
                )
                columnY[column] += placeable.height
            }
        }
    }
}