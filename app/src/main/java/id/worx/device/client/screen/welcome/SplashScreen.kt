package id.worx.device.client.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.device.client.R
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.WorxTheme

@Composable
fun SplashScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.primary
    ) {
        Image(
            modifier = Modifier.padding(horizontal = 98.dp),
            alignment = Alignment.Center,
            painter = painterResource(R.drawable.worx_logo),
            contentDescription = "Worx Logo")
    }
}


@Preview(name = "Splash Screen", showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    WorxTheme {
        SplashScreen()
    }
}
