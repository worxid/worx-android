package id.worx.device.client.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.fieldmodel.BarcodeField
import id.worx.device.client.model.fieldmodel.BarcodeFieldValue
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus
import id.worx.device.client.viewmodel.ScannerViewModel

enum class BarcodeType(val type: String) {
    All("all"),
    D1("1d"),
    D2("2d")
}

@Composable
fun WorxBarcodeField(
    indexForm: Int,
    viewModel: DetailFormViewModel,
    scannerViewModel: ScannerViewModel,
    session: Session,
    validation: Boolean = false
) {
    val theme = session.theme
    val form =
        viewModel.uiState.collectAsState().value.detailForm?.fields?.get(indexForm) as BarcodeField
    val barcodeFieldValue =
        viewModel.uiState.collectAsState().value.values[form.id] as BarcodeFieldValue?
    val formStatus = viewModel.uiState.collectAsState().value.status
    val manuallyOverride = form.manuallyOverride
    var barcodeValue by remember { mutableStateOf(barcodeFieldValue?.value ?: "") }
    if (barcodeFieldValue?.value != null) {
        viewModel.setComponentData(indexForm, BarcodeFieldValue(value = barcodeFieldValue.value))
    }
    val warningInfo = if (form.required == true && barcodeValue.isBlank()) {
        "${form.label} is required"
    } else if (form.barcodeType == BarcodeType.All.type) {
        "Limited to 1D barcodes only"
    } else {
        ""
    }

    WorxBaseField(
        indexForm = indexForm,
        viewModel = viewModel,
        validation = validation,
        session = session,
        warningInfo = warningInfo
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    placeholder = {
                        Text(
                            text = "Scan Barcode",
                            style = Typography.body2.copy(
                                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.54f)
                            )
                        )
                    },
                    value = barcodeValue,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .weight(1f)
                        .clickable {
                            if (!manuallyOverride!!) {
                                scannerViewModel.navigateFromDetailScreen(
                                    indexForm,
                                    type = form.barcodeType ?: BarcodeType.All.type
                                )
                                viewModel.goToScannerBarcode(indexForm)
                            }
                        },
                    enabled = manuallyOverride ?: false,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Black.copy(0.06f)
                    ),
                    textStyle = if (barcodeValue.isEmpty()) {
                        Typography.body2.copy(color = MaterialTheme.colors.onSecondary.copy(0.54f))
                    } else {
                        Typography.body2.copy(MaterialTheme.colors.onSecondary)
                    },
                    shape = RoundedCornerShape(4.dp),
                    trailingIcon = {
                        if (!arrayListOf(
                                EventStatus.Done,
                                EventStatus.Submitted
                            ).contains(formStatus)
                        ) {
                            Icon(
                                painterResource(id = R.drawable.ic_delete_circle),
                                contentDescription = "Clear Text",
                                modifier = Modifier
                                    .clickable {
                                        viewModel.setComponentData(indexForm, null)
                                        barcodeValue = ""
                                    },
                                tint = MaterialTheme.colors.onSecondary,
                            )
                        }
                    },
                    onValueChange = {
                        barcodeValue = it
                        if (it.isEmpty()) {
                            viewModel.setComponentData(indexForm, null)
                        } else {
                            viewModel.setComponentData(
                                indexForm,
                                BarcodeFieldValue(value = barcodeValue)
                            )
                        }
                    }
                )
                Box(modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (theme == SettingTheme.Dark) Color.White else MaterialTheme.colors.background.copy(
                            0.10f
                        )
                    )
                    .clickable {
                        scannerViewModel.navigateFromDetailScreen(
                            indexForm,
                            type = form.barcodeType ?: BarcodeType.All.type
                        )
                        viewModel.goToScannerBarcode(indexForm)
                    }
                    .size(48.dp)
                    .height(TextFieldDefaults.MinHeight)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_barcode),
                        contentDescription = "Barcode Scanner",
                        modifier = Modifier.align(Alignment.Center),
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            }
        }
    }
}