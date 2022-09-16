package id.worx.device.client.screen.components

import android.graphics.Bitmap
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import id.worx.device.client.R
import id.worx.device.client.model.SignatureField
import id.worx.device.client.model.SignatureValue
import id.worx.device.client.screen.ActionRedButton
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxSignature(indexForm: Int, viewModel: DetailFormViewModel) {
    val form = viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as SignatureField

    val value = viewModel.uiState.collectAsState().value.values[form.id] as SignatureValue?
    var bitmap = if (value == null) {
        remember {
            mutableStateOf<Bitmap?>(null)
        }
    } else {
        remember { mutableStateOf<Bitmap?>(value.bitmap) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            form.label ?: "Signature",
            style = Typography.body2,
            modifier = Modifier.padding(start = 17.dp, bottom = 8.dp, end = 16.dp)
        )
        if (bitmap.value != null) {
            SignatureView(bitmap.value) {
                viewModel.setComponentData(indexForm, null)
                bitmap.value = null
            }
        } else {
            AttachSignatureButton {
                viewModel.goToSignaturePad(indexForm)
            }
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun AttachSignatureButton(
    goToSignaturePad: () -> Unit
) {
    ActionRedButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        iconRes = R.drawable.ic_signature_icon,
        title = stringResource(id = R.string.add_signature),
        actionClick = { goToSignaturePad() }
    )
}

@Composable
private fun SignatureView(
    bitmap: Bitmap?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                .fillMaxWidth(0.6f)
                .height(102.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                contentScale = ContentScale.Fit,
                model = bitmap,
                contentDescription = "Signature"
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            modifier = Modifier
                .padding(start = 30.dp, end = 4.dp)
                .clickable { onClick() }
                .align(Alignment.Top),
            painter = painterResource(id = R.drawable.ic_delete_circle),
            contentDescription = "Clear File"
        )
    }
}