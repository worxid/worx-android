package id.worx.mobile.screen.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.worx.mobile.R
import id.worx.mobile.theme.LocalWorxColorsPalette
import id.worx.mobile.theme.WorxTheme

@Composable
fun SplashScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.primary
    ) {
        Box {
            Image(
                modifier = Modifier.fillMaxSize()
                    .background(color = LocalWorxColorsPalette.current.splashBackground),
                contentScale = ContentScale.Crop,
                painter = painterResource(R.drawable.grid_bg),
                contentDescription = "Background")
            Image(
                modifier = Modifier.padding(horizontal = 98.dp)
                    .align(Alignment.Center),
                painter = painterResource(R.drawable.worx_logo_white),
                contentDescription = "Worx Logo")
        }
    }
}


@Preview(name = "Splash Screen", showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    WorxTheme {
        SplashScreen()
    }
}
