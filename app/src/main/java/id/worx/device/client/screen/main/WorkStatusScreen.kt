package id.worx.device.client.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.device.client.screen.components.RedFullWidthButton
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.HomeViewModel
import id.worx.device.client.viewmodel.HomeViewModelImpl

@Composable
fun WorkStatusScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModelImpl,
    theme: String?,
    onBack: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()

    val statuses = listOf("Available", "Break", "On going", "Meeting")

    var onCheck by remember {
        mutableStateOf<String>(uiState.workStatus)
    }

    Scaffold(
        topBar = {
            WorxTopAppBar(onBack = {
                onBack()
            }, title = "Work Status", useProgressBar = false)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (theme == SettingTheme.Dark
                    ) Color.Unspecified else Color.White
                )
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(statuses) { index, status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (theme == SettingTheme.Dark
                                ) Color.Unspecified else Color.White
                            )
                            .border(
                                1.dp,
                                color = if (theme == SettingTheme.Dark)
                                    MaterialTheme.colors.onSecondary
                                else MaterialTheme.colors.onSecondary.copy(0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .selectable(
                                selected = (status == onCheck),
                                onClick = {
                                    onCheck = status
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (status == onCheck),
                            onClick = {
                                onCheck = status
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colors.onBackground,
                                unselectedColor = MaterialTheme.colors.onSecondary
                            ),
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = status, style = Typography.body1.copy(
                                color = Color.Black
                            )
                        )
                    }
                }
            }
            RedFullWidthButton(
                onClickCallback = {
                    homeViewModel.saveWorkStatus(onCheck)
                }, label = "Save", modifier = Modifier.align(
                    Alignment.BottomCenter
                ), theme = theme
            )
        }

    }
}

@Preview
@Composable
fun WorkStatusScreenPreview() {
    WorxTheme {
//        WorkStatusScreen(theme = "")
    }
}