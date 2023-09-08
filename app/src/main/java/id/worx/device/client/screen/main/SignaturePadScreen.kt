package id.worx.device.client.screen.main

import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import id.worx.device.client.R
import id.worx.device.client.screen.components.WorxTopAppBar
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxCustomColorsPalette
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
    var isSigned by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            WorxTopAppBar(
                onBack = {},
                title = "Digital Signature",
                useProgressBar = false,
                isShowBackButton = false,
            )
        }
    ) { padding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(WorxCustomColorsPalette.current.homeBackground)
        ) {
            val (
                signaturePad,
                txtDrawHere,
                txtClearSignature,
                actionButton,
            ) = createRefs()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((configuration.screenWidthDp / 2).dp)
                    .padding(vertical = 8.dp, horizontal = 12.dp)
                    .background(WorxCustomColorsPalette.current.bottomSheetBackground)
                    .border(1.5.dp, WorxCustomColorsPalette.current.signaturePadBorder)
                    .constrainAs(signaturePad) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(actionButton.top)
                    }
            ) {
                SignaturePadView(
                    penColor = WorxCustomColorsPalette.current.signaturePenColor,
                    onReady = {
                        signaturePadAdapter = it
                    },
                    onStartSigning = {
                        isSigned = true
                    },
                )
            }

            if (!isSigned) {
                Text(
                    text = "Draw here",
                    color = WorxCustomColorsPalette.current.unselectedStar,
                    modifier = Modifier.constrainAs(txtDrawHere) {
                        top.linkTo(signaturePad.top)
                        bottom.linkTo(signaturePad.bottom)
                        start.linkTo(signaturePad.start)
                        end.linkTo(signaturePad.end)
                    }
                )
            }

            Text(
                stringResource(id = R.string.clear_signature),
                color = WorxCustomColorsPalette.current.button.copy(
                    alpha = if (isSigned) 1.0f else 0.3f
                ),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        if (isSigned) {
                            signaturePadAdapter!!.clear()
                            isSigned = false
                        }
                    }
                    .constrainAs(txtClearSignature) {
                        top.linkTo(signaturePad.bottom, 36.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
                    .background(Color.Black)
                    .constrainAs(actionButton) {
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                val (
                    txtCancel,
                    txtConfirm,
                ) = createRefs()

                Text(
                    text = stringResource(R.string.cancel),
                    color = Color.White,
                    modifier = Modifier
                        .clickable {
                            onBackNavigation()
                        }
                        .constrainAs(txtCancel) {
                            top.linkTo(parent.top, 30.dp)
                            bottom.linkTo(parent.bottom, 30.dp)
                            start.linkTo(parent.start, 16.dp)
                        }
                )

                Text(
                    text = stringResource(R.string.confirm),
                    color = Color.White.copy(
                        alpha = if (isSigned) 1.0f else 0.3f
                    ),
                    modifier = Modifier
                        .clickable {
                            if (isSigned) {
                                viewModel.saveSignature(
                                    signaturePadAdapter!!.getSignatureBitmap(),
                                    viewModel.uiState.value.currentComponent
                                )
                            }
                        }
                        .constrainAs(txtConfirm) {
                            top.linkTo(parent.top, 30.dp)
                            bottom.linkTo(parent.bottom, 30.dp)
                            end.linkTo(parent.end, 16.dp)
                        }
                )
            }

//            Spacer(modifier = Modifier.weight(1f))
//            Row(modifier = Modifier.fillMaxWidth()) {
//                OutlinedButton(
//                    modifier = Modifier
//                        .padding(end = 16.dp)
//                        .weight(1f),
//                    colors = ButtonDefaults.buttonColors(
//                        backgroundColor = MaterialTheme.colors.secondary.copy(0.1f),
//                        contentColor = MaterialTheme.colors.onSecondary
//                    ),
//                    border = BorderStroke(1.5.dp, MaterialTheme.colors.onSecondary),
//                    shape = RoundedCornerShape(1),
//                    onClick = {
//                        signaturePadAdapter!!.clear()
//                    }
//                ) {
//                    Text(
//                        modifier = Modifier.padding(vertical = 12.dp),
//                        text = stringResource(R.string.clear),
//                        style = Typography.button
//                    )
//                }
//                OutlinedButton(
//                    modifier = Modifier.weight(1f),
//                    colors = ButtonDefaults.buttonColors(
//                        backgroundColor = MaterialTheme.colors.onBackground,
//                        contentColor = Color.White
//                    ),
//                    border = BorderStroke(1.5.dp, MaterialTheme.colors.onSecondary),
//                    shape = RoundedCornerShape(1),
//                    onClick = {
//                        viewModel.saveSignature(signaturePadAdapter!!.getSignatureBitmap(),
//                            viewModel.uiState.value.currentComponent)
//                    }
//                ) {
//                    Text(
//                        modifier = Modifier.padding(vertical = 12.dp),
//                        text = stringResource(R.string.save_eng),
//                        style = Typography.button
//                    )
//                }

//            }
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