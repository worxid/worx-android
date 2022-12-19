package id.worx.device.client.screen.main

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import id.worx.device.client.R
import id.worx.device.client.model.BarcodeFieldValue
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.fontRoboto
import id.worx.device.client.util.BarcodeAnalyzer
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.ScannerViewModel
import java.io.File
import kotlin.math.log

@Composable
fun BarcodePreviewScreen(
    viewModel: DetailFormViewModel,
    scannerViewModel: ScannerViewModel
) {
    val index = scannerViewModel.indexForm.value
    val filePath = scannerViewModel.photoPath.value
    val context = LocalContext.current
    val options = BarcodeScannerOptions.Builder()
    val barcodeType = scannerViewModel.type.value

    Log.d("TAG", "BarcodePreviewScreen: $barcodeType")
    if (barcodeType == "all") {
        options.setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
    } else {
        options.setBarcodeFormats(
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.TYPE_ISBN,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_ITF,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODABAR
        )
    }
    val barcodeScanner = BarcodeScanning.getClient(options.build())
    val inputImage = InputImage.fromFilePath(context, Uri.fromFile(File(filePath!!)))

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.onError,
                contentColor = MaterialTheme.colors.onError
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 14.dp)
                ) {
                    val (tvCancel, tvPreview) = createRefs()
                    Text(
                        text = stringResource(id = R.string.cancel),
                        style = Typography.subtitle1.copy(
                            color = MaterialTheme.colors.onBackground,
                            fontFamily = fontRoboto
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.constrainAs(tvCancel) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.preview),
                        style = Typography.subtitle1.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = fontRoboto
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.constrainAs(tvPreview) {
                            top.linkTo(tvCancel.top)
                            bottom.linkTo(tvCancel.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    ) { _ ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.onError)
        ) {
            val (image, container) = createRefs()
            AsyncImage(
                model = if (filePath?.contains("File") == true) {
                    android.R.drawable.ic_menu_gallery
                } else {
                    filePath
                },
                contentDescription = "Image",
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                contentScale = ContentScale.Fit
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.54f))
                    .height(80.dp)
                    .padding(horizontal = 24.dp)
                    .constrainAs(container) {
                        bottom.linkTo(parent.bottom)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_image),
                    tint = Color.White,
                    contentDescription = "Collection Image"
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        barcodeScanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                if (barcodes.isNotEmpty()) {
                                    barcodes.forEach { it ->
                                        it.rawValue?.let {
                                            if (index != null) {
                                                viewModel.setComponentData(index, BarcodeFieldValue(value = it))
                                                scannerViewModel.setResult(it)
                                                scannerViewModel.navigateToDetail()
                                            }
                                        }
                                    }
                                } else {
                                    val typeBarcode = if (barcodeType == "1d"){ " ${context.getString(R.string.only_1d)}" } else ""
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.no_barcode) + typeBarcode.ifBlank { "" },
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(
                                    "Barocde Analyzer",
                                    "Barocde Analyzer: Something went wrong $e"
                                )
                            }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_confirm),
                        contentDescription = "Confirm photo",
                        tint = Color.White,
                    )
                    Text(
                        text = stringResource(id = R.string.done).uppercase(),
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 8.dp),
                        fontFamily = fontRoboto
                    )
                }
            }
        }
    }
}