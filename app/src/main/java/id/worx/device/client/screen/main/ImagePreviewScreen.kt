package id.worx.device.client.screen.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import id.worx.device.client.viewmodel.HomeViewModelImpl

@Composable
fun ImagePreviewScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModelImpl,
    onClose: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.Black
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(modifier = Modifier, onClick = {
                        onClose()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                backgroundColor = Color.Black
            ) {

            }
        }
    ) { paddingValues ->
        AsyncImage(
            model = "Image Path",
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}