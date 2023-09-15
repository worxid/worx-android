package id.worx.mobile.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
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
import id.worx.mobile.R
import id.worx.mobile.model.FormSortModel
import id.worx.mobile.model.FormSortOrderBy
import id.worx.mobile.model.FormSortType
import id.worx.mobile.theme.LocalWorxColorsPalette
import id.worx.mobile.theme.PrimaryMain
import id.worx.mobile.theme.WorxTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorxSortByBottomSheet(
    sheetState: ModalBottomSheetState,
    selectedSort: FormSortModel,
    onSortClicked: (newSort: FormSortModel) -> Unit,
    content: @Composable (openSortByBottomSheet: () -> Unit) -> Unit,
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            WorxSortByContent(
                selectedSort = selectedSort, onSortClicked = {
                    onSortClicked.invoke(it)
                    scope.launch { sheetState.hide() }
                }
            )
        },
        sheetBackgroundColor = LocalWorxColorsPalette.current.bottomSheetBackground
    ) {
        content(openSortByBottomSheet = { scope.launch { sheetState.show() } })
    }
}

@Composable
private fun WorxSortByContent(
    selectedSort: FormSortModel,
    onSortClicked: (newSort: FormSortModel) -> Unit
) {
    val colorPalette = LocalWorxColorsPalette.current
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
                .align(Alignment.Center), onDraw = {
                drawRoundRect(
                    color = colorPalette.bottomSheetDragHandle,
                    cornerRadius = CornerRadius(2f, 2f)
                )
            })
        }
        Text(
            text = stringResource(R.string.text_sort_by),
            style = MaterialTheme.typography.h6.copy(),
            color = MaterialTheme.colors.onSecondary.copy(0.87f),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )
        for (model in FormSortType.values()) {
            val isSelected = model.value == selectedSort.formSortType.value
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) PrimaryMain.copy(alpha = 0.16f) else colorPalette.bottomSheetBackground)
                    .clickable {
                        if (isSelected) {
                            onSortClicked(selectedSort.toggleSortOrderBy())
                        } else {
                            onSortClicked(FormSortModel(model))
                        }
                    }
                    .padding(
                        horizontal = if (isSelected) 16.dp else 36.dp,
                        vertical = 10.dp
                    )
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = if (selectedSort.formSortOrderBy == FormSortOrderBy.ASC) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                        contentDescription = "Arrow Upward",
                        tint = colorPalette.button,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = model.value,
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
fun WorxSortByBottomSheet_Preview() {
    WorxTheme {
        val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        WorxSortByBottomSheet(
            sheetState = sheetState,
            selectedSort = FormSortModel(),
            onSortClicked = {}
        ) {}
    }
}