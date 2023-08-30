package id.worx.device.client.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import id.worx.device.client.R
import id.worx.device.client.model.FormSortModel
import id.worx.device.client.model.FormSortOrderBy
import id.worx.device.client.model.FormSortType
import id.worx.device.client.theme.DragHandle
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.WorxTheme
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
        modifier = Modifier.zIndex(10f),
        sheetState = sheetState,
        sheetContent = {
            WorxSortByContent(selectedSort = selectedSort, onSortClicked = onSortClicked)
        },
        sheetBackgroundColor = Color.White,
        sheetContentColor = Color.White
    ) {
        content(openSortByBottomSheet = { scope.launch { sheetState.show() } })
    }
}

@Composable
private fun WorxSortByContent(
    selectedSort: FormSortModel,
    onSortClicked: (newSort: FormSortModel) -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp)
        ) {
            Canvas(modifier = Modifier
                .size(80.dp, 4.dp)
                .align(Alignment.Center), onDraw = {
                drawRoundRect(
                    color = DragHandle,
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
                    .background(if (isSelected) PrimaryMain.copy(alpha = 0.16f) else Color.White)
                    .padding(
                        horizontal = if (isSelected) 16.dp else 36.dp,
                        vertical = 10.dp
                    )
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = if (selectedSort.formSortOrderBy == FormSortOrderBy.ASC) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                        contentDescription = "Arrow Upward",
                        tint = PrimaryMain,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = model.value,
                    style = MaterialTheme.typography.body1,
                    fontSize = 16.sp,
                    color = if (isSelected) PrimaryMain else MaterialTheme.colors.onSecondary.copy(
                        alpha = 0.87f
                    )
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