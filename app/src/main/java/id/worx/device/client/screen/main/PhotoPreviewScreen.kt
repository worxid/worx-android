package id.worx.device.client.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import id.worx.device.client.model.ImageValue
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel
import java.io.File

@Composable
fun PhotoPreviewScreen(
    viewModel: CameraViewModel,
    detailViewModel: DetailFormViewModel,
    addPhotoToGallery: (String) -> Unit
) {
    val detailUiState = detailViewModel.uiState.collectAsState()

    val path = viewModel.photoPath.value!!
    val index = viewModel.indexForm.value!!
    val id = detailUiState.value.detailForm!!.fields[index].id
    val value = detailUiState.value.values[id] as ImageValue?
    val filePath = value?.filePath ?: arrayListOf()

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = viewModel.photoPath.value?.let { File(it) },
            contentDescription = "Image",
            modifier = Modifier
                .fillMaxSize()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(Color.Black.copy(0.54f))
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
                viewModel.rejectPhoto()
            }, modifier = Modifier.padding(8.dp)) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Retake".uppercase(), fontSize = 16.sp,
                    color = Color.White,

                    )
            }
            TextButton(onClick = {
                filePath.add(path)
                detailViewModel.getPresignedUrl(filePath, index, 2)
                addPhotoToGallery(path)
                viewModel.navigateToDetail()
            }, modifier = Modifier.padding(8.dp)) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Done".uppercase(), fontSize = 16.sp,
                    color = Color.White,
                )
            }
        }
    }
}

//@Preview
//@Composable
//private fun PreviewPhotoScreen() {
//    val viewModel: CameraViewModel = hiltViewModel()
//    val detailViewModel: DetailFormViewModel = hiltViewModel()
//    WorxTheme() {
//        PhotoPreviewScreen(viewModel = viewModel, detailViewModel) {}
//    }
//}

//@Composable
//fun TestScreen(
//    viewModel: CameraViewModel,
//    detailViewModel: DetailFormViewModel,
//    addPhotoToGallery: (String) -> Unit
//) {
//    val model = viewModel.photoPath.value?.let { File(it) }
//    val onRejectPhotoClicked = viewModel.rejectPhoto()
//    val path = viewModel.photoPath.value!!
//    val index = viewModel.indexForm.value!!
//    val state = detailViewModel.uiState.collectAsState()
//    val id = state.value.detailForm!!.fields[index].id
//    val value = state.value.values[id] as ImageValue?
//    val filePath = value?.filePath ?: arrayListOf()
//
//    // taro di onclick
//
//    TestChildScreen(model, { onRejectPhotoClicked }) {
//        filePath.add(path)
//        detailViewModel.getPresignedUrl(filePath, index, 2)
//        addPhotoToGallery(path)
//        viewModel.navigateToDetail()
//    }
//}
//
//@Composable
//fun TestChildScreen(model: File?, function: () -> Unit, function1: () -> Unit) {
//    Box(modifier = Modifier.fillMaxSize()) {
//        AsyncImage(
//            model = model,
//            contentDescription = "Image",
//            modifier = Modifier
//                .fillMaxSize()
//        )
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(110.dp)
//                .background(Color.Black.copy(0.54f))
//                .align(Alignment.BottomCenter),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            TextButton(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
//                Icon(
//                    imageVector = Icons.Default.Refresh,
//                    contentDescription = null,
//                    tint = Color.White,
//                    modifier = Modifier.size(16.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "Retake".uppercase(), fontSize = 16.sp,
//                    color = Color.White,
//
//                    )
//            }
//            TextButton(onClick = { /*TODO*/ }, modifier = Modifier.padding(8.dp)) {
//                Icon(
//                    imageVector = Icons.Default.Done,
//                    contentDescription = null,
//                    tint = Color.White,
//                    modifier = Modifier.size(16.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "Done".uppercase(), fontSize = 16.sp,
//                    color = Color.White,
//                )
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//fun TesPreview() {
//    TestChildScreen(model = null, function = {}) {
//
//    }
//
//}