package id.worx.device.client.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.CameraViewModel
import java.io.File

@Composable
fun PhotoPreviewScreen(viewModel: CameraViewModel){
     Box(modifier = Modifier.fillMaxSize()){
         AsyncImage(
             model = viewModel.photoPath.value?.let { File(it) },
             contentDescription = "Image",
             modifier = Modifier
                 .fillMaxSize()
         )
         Row(modifier = Modifier
             .fillMaxWidth()
             .height(110.dp)
             .background(Color.Black.copy(0.54f))
             .align(Alignment.BottomCenter),
         verticalAlignment = Alignment.CenterVertically,
         horizontalArrangement = Arrangement.SpaceBetween) {
             Text(
                 text = "x",
                 fontSize = 36.sp,
                 color = Color.White,
                 modifier = Modifier.padding(16.dp)
                     .clickable { viewModel.rejectPhoto() }
                 )
             Text(
                 text = "✔️",
                 fontSize = 36.sp,
                 color = Color.White,
                 modifier = Modifier.padding(16.dp)
                     .clickable {  }
             )
         }
     }
}

@Preview
@Composable
private fun PreviewPhotoScreen(){
    val viewModel : CameraViewModel = hiltViewModel()
    WorxTheme() {
        PhotoPreviewScreen(viewModel = viewModel)
    }
}