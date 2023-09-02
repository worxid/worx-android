package id.worx.device.client.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.worx.device.client.R
import id.worx.device.client.screen.main.AppTheme
import id.worx.device.client.theme.WorxCustomColorsPalette
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.WorxTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorxThemeBottomSheet(
    sheetState: ModalBottomSheetState,
    selectedTheme: AppTheme,
    onThemeClicked: (theme: AppTheme) -> Unit,
    content: @Composable (openSortByBottomSheet: () -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            WorxThemeContent(
                selectedTheme = selectedTheme,
                onThemeClicked = {
                    onThemeClicked.invoke(it)
                    scope.launch { sheetState.hide() }
                }
            )
        },
        sheetBackgroundColor = WorxCustomColorsPalette.current.bottomSheetBackground
    ) {
        content(openSortByBottomSheet = { scope.launch { sheetState.show() } })
    }
}

@Composable
private fun WorxThemeContent(
    selectedTheme: AppTheme,
    onThemeClicked: (theme: AppTheme) -> Unit
) {
    val colorPalette = WorxCustomColorsPalette.current
    Column(
        modifier = Modifier
            .background(colorPalette.bottomSheetBackground)
            .padding(bottom = 16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorPalette.bottomSheetBackground)
                .padding(vertical = 8.dp)
        ) {
            Canvas(modifier = Modifier
                .size(80.dp, 4.dp)
                .align(Alignment.Center),
                onDraw = {
                    drawRoundRect(
                        color = colorPalette.bottomSheetDragHandle,
                        cornerRadius = CornerRadius(2f, 2f)
                    )
                })
        }
        Text(
            text = stringResource(R.string.theme),
            style = MaterialTheme.typography.h6.copy(),
            color = MaterialTheme.colors.onSecondary.copy(0.87f),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )
        for (model in AppTheme.values()) {
            val isSelected = model.theme == selectedTheme.theme
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) PrimaryMain.copy(alpha = 0.16f) else colorPalette.bottomSheetBackground)
                    .clickable { onThemeClicked(model) }
                    .padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    )
            ) {
                Text(
                    text = model.theme,
                    style = MaterialTheme.typography.body1,
                    fontSize = 16.sp,
                    color = if (isSelected) colorPalette.button else MaterialTheme.colors.onSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun WorxThemeBottomSheet_Preview() {
    WorxTheme {
        val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        WorxThemeBottomSheet(
            sheetState = sheetState,
            selectedTheme = AppTheme.LIGHT,
            onThemeClicked = {}
        ) {}
    }
}