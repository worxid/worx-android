package id.worx.device.client.screen.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.worx.device.client.model.fieldmodel.DropDownField
import id.worx.device.client.model.fieldmodel.DropDownValue
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionMenuScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailFormViewModel,
    navigateBack: () -> Unit
) {
    val indexForm = viewModel.itemIndex.value
    val form =
        viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as DropDownField
    val optionTitles = form.options

    val value = viewModel.uiState.collectAsState().value.values[form.id] as DropDownValue?

    val onCheck = if (value != null){
        remember {
            mutableStateOf(value.value)
        }
    } else {
        remember {
            mutableStateOf<Int?>(null)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material.IconButton(
                        onClick = {
                            navigateBack()
                        },
                    ) {
                        androidx.compose.material.Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back Button"
                        )
                    }
                    androidx.compose.material.Text(
                        text = "Select an option",
                        style = Typography.h6,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = true),
                        textAlign = TextAlign.Center
                    )
                    if (onCheck.value != null) {
                        androidx.compose.material.TextButton(
                            modifier = Modifier,
                            onClick = {
                                onCheck.value = null
                                viewModel.setComponentData(indexForm, DropDownValue(value = null))
                                coroutineScope.launch {
                                    delay(250)
                                    navigateBack()
                                }
                            }) {
                            androidx.compose.material.Text(
                                text = "Clear",
                                style = Typography.body1,
                                color = MaterialTheme.colors.onPrimary
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(optionTitles) { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (index == onCheck.value),
                            onClick = {
                                onCheck.value = index
                                viewModel.setComponentData(indexForm, DropDownValue(value = onCheck.value))
                                coroutineScope.launch {
                                    delay(250)
                                    navigateBack()
                                }
                            }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (index == onCheck.value),
                        onClick = {
                            onCheck.value = index
                            viewModel.setComponentData(indexForm, DropDownValue(value = onCheck.value))
                            coroutineScope.launch {
                                delay(250)
                                navigateBack()
                            }
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colors.primary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    option.label?.let { Text(text = it) }
                }
            }
        }
    }
}