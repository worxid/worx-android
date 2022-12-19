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
import id.worx.device.client.model.BarcodeField
import id.worx.device.client.model.BarcodeFieldValue
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus
import id.worx.device.client.viewmodel.ScannerViewModel

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
    val barcodeValue =
        viewModel.uiState.collectAsState().value.values[form.id] as BarcodeFieldValue?
    val formStatus = viewModel.uiState.collectAsState().value.status
    var value by remember {
        mutableStateOf(
            barcodeValue?.value
        )
    }
    if (barcodeValue?.value != null){
        viewModel.setComponentData(indexForm, BarcodeFieldValue(value = barcodeValue.value))
    }
    val warningInfo = if (form.required == true && value == null) {
        "${form.label} is required"
    } else if (form.barcodeType == "all") {
//        TODO("barcodeType need to refactor")
        "Limited to 1D barcodes only"
    } else {
        ""
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            form.label ?: "",
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (!form.description.isNullOrBlank()) {
            Text(
                text = form.description!!,
                color = if (theme == SettingTheme.Dark) textFormDescriptionDark else textFormDescription,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = if (value == null) "Scan Barcode" else value ?: "",
                modifier = Modifier.padding(end = 12.dp),
                enabled = false,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Black.copy(0.06f)
                ),
                textStyle = if (value.isNullOrEmpty()) {
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
                                    value = null
                                },
                            tint = MaterialTheme.colors.onSecondary,
                        )
                    }
                },
                onValueChange = {})
            Box(modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (theme == SettingTheme.Dark) Color.White else MaterialTheme.colors.background.copy(
                        0.10f
                    )
                )
                .clickable {
                    scannerViewModel.navigateFromDetailScreen(indexForm, type = form.barcodeType ?: "")
                    viewModel.goToScannerBarcode(indexForm)
                }
                .fillMaxSize()
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

        if (warningInfo.isNotBlank()) {
            if (validation) {
                Text(
                    text = warningInfo,
                    modifier = Modifier.padding(top = 4.dp),
                    color = PrimaryMain
                )
            }
            form.isValid = false
        } else {
            form.isValid = true
        }
    }
}