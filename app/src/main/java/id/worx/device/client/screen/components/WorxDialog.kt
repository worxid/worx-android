package id.worx.device.client.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                .background(Color.Black)
                .padding(top = 2.dp, start = 2.dp, end = 3.dp, bottom = 3.dp)
                .background(Color.White)
                .padding(20.dp)
                .background(Color.White),
            content = content
        )
    }
}