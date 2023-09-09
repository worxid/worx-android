package id.worx.device.client.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope

@Composable
fun WorxDialog(
    content : @Composable ConstraintLayoutScope.() -> Unit,
    setShowDialog: (Boolean) -> Unit
) {
    Dialog(onDismissRequest = { setShowDialog(false) }, properties = DialogProperties()) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.secondary)
                .padding(16.dp),
            content = content
        )
    }
}