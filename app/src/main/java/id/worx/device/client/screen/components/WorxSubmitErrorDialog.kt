package id.worx.device.client.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import id.worx.device.client.R
import id.worx.device.client.theme.LocalWorxColorsPalette
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme

enum class SubmitErrorDialogType(val resId: Int) {
    FAILED(R.drawable.ic_failed),
    WARNING(R.drawable.ic_warning)
}

@Composable
fun WorxSubmitErrorDialog(
    title: String,
    description: String,
    submitErrorDialogType: SubmitErrorDialogType,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = Typography.h6,
                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.87f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                modifier = Modifier.size(56.dp),
                painter = painterResource(id = submitErrorDialogType.resId),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = Typography.body2,
                color = MaterialTheme.colors.onSecondary.copy(alpha = 0.87f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDismiss, modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth()
                    .border(1.5.dp, color = MaterialTheme.colors.onSecondary),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = LocalWorxColorsPalette.current.formItemContainer
                )
            ) {
                Text(
                    text = "OK",
                    style = Typography.button,
                    color = LocalWorxColorsPalette.current.button,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WorxSubmitErrorDialogPreview() {
    WorxTheme {
        WorxSubmitErrorDialog(
            title = "Failed to Upload Media",
            description = "Please check your connection and try to upload again.",
            submitErrorDialogType = SubmitErrorDialogType.FAILED,
            onDismiss = {})
        WorxSubmitErrorDialog(
            title = "Failed to upload media",
            description = "Please wait until it is finished before submitting the form.",
            submitErrorDialogType = SubmitErrorDialogType.WARNING,
            onDismiss = {})
    }
}