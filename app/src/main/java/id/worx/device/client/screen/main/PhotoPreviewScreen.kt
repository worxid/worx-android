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
import id.worx.device.client.viewmodel.DetailFormViewModel
import java.io.File

@Composable
fun PhotoPreviewScreen(viewModel: CameraViewModel, detailViewModel:DetailFormViewModel){
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
                 modifier = Modifier
                     .padding(horizontal = 36.dp, vertical = 20.dp)
                     .clickable { viewModel.rejectPhoto() }
                 )
             Text(
                 text = "\u2713",
                 fontSize = 36.sp,
                 color = Color.White,
                 modifier = Modifier
                     .padding(horizontal = 36.dp, vertical = 20.dp)
                     .clickable {
                         val path = viewModel.photoPath.value!!
                         val index = viewModel.indexForm.value!!
                         detailViewModel.setComponentData(index, path)
                         viewModel.navigateToDetail()
                     }
             )
         }
     }
}

@Preview
@Composable
private fun PreviewPhotoScreen(){
    val viewModel : CameraViewModel = hiltViewModel()
    val detailViewModel: DetailFormViewModel = hiltViewModel()
    WorxTheme() {
        PhotoPreviewScreen(viewModel = viewModel, detailViewModel)
    }
}