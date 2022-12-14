package id.worx.device.client.screen.main

import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.R
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.DetailFormViewModel
import se.warting.signaturepad.SignaturePadAdapter
import se.warting.signaturepad.SignaturePadView
import java.io.ByteArrayOutputStream

@Composable
fun SignaturePadScreen(
    viewModel: DetailFormViewModel,
    onBackNavigation: () -> Unit
) {
    var signaturePadAdapter: SignaturePadAdapter? = null
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = onBackNavigation,
                title = "Signature",
                useProgressBar = false
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.secondary)
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .height((configuration.screenWidthDp / 2).dp)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .border(1.5.dp, MaterialTheme.colors.onSecondary, RoundedCornerShape(4.dp))
            ) {
                SignaturePadView(onReady = {
                    signaturePadAdapter = it
                })
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondary.copy(0.1f),
                        contentColor = MaterialTheme.colors.onSecondary
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colors.onSecondary),
                    shape = RoundedCornerShape(1),
                    onClick = {
                        signaturePadAdapter!!.clear()
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = stringResource(R.string.clear),
                        style = Typography.button
                    )
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.onBackground,
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.5.dp, MaterialTheme.colors.onSecondary),
                    shape = RoundedCornerShape(1),
                    onClick = {
                        viewModel.saveSignature(signaturePadAdapter!!.getSignatureBitmap(),
                            viewModel.uiState.value.currentComponent)
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = stringResource(R.string.save_eng),
                        style = Typography.button
                    )
                }

            }
        }
    }
}

fun getImageUri(inContext: Context, inImage: Bitmap): String {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    return MediaStore.Images.Media.insertImage(
        inContext.getContentResolver(),
        inImage,
        "Signature",
        null
    )
}

@Preview(name = "PreviewSignatureScreen", showSystemUi = true)
@Composable
fun PreviewSignatureScreen() {
    val viewModel: DetailFormViewModel = hiltViewModel()
    WorxTheme() {
        SignaturePadScreen(viewModel, {})
    }
}