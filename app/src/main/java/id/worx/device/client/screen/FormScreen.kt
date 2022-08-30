package id.worx.device.client.screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import id.worx.device.client.theme.Typography

@Composable
fun FormScreen(text: String){
    Text(text =text, style = Typography.h5)
}