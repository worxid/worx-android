package id.worx.device.client.screen.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import id.worx.device.client.R
import id.worx.device.client.screen.ActionRedButton
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel

@Composable
fun WorxSignature(indexForm: Int, viewModel: DetailFormViewModel) {
    val form = viewModel.uiState.detailForm!!.componentList[indexForm]
    var filePath by remember { mutableStateOf(form.Outputdata) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            form.inputData.title,
            style = Typography.body2,
            modifier = Modifier.padding(start = 17.dp, bottom = 8.dp, end = 16.dp)
        )
        if (!filePath.isNullOrEmpty()) {
            SignatureView(filePath!!) {
                viewModel.setComponentData(indexForm, null)
                filePath = null
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
    bitmapPath: String,
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
                model = bitmapPath,
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