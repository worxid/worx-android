package id.worx.device.client.screen.main

import android.net.Uri
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import id.worx.device.client.MainScreen
import id.worx.device.client.R
import id.worx.device.client.model.fieldmodel.ImageValue
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.theme.fontRoboto
import id.worx.device.client.viewmodel.CameraViewModel
import id.worx.device.client.viewmodel.DetailFormViewModel
import java.io.File

@Composable
fun PhotoPreviewScreen(
    viewModel: CameraViewModel,
    detailViewModel: DetailFormViewModel,
    addPhotoToGallery: (String) -> Unit
) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher

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
            Row(
                modifier = Modifier
                    .padding(start = 24.dp, top = 20.dp, bottom = 20.dp)
                    .clickable { viewModel.rejectPhoto() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_retake),
                    contentDescription = "Retake photo",
                    tint = Color.White,
                )
                Text(
                    text = stringResource(id = R.string.retake).uppercase(),
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 8.dp),
                    fontFamily = fontRoboto,
                )
            }
            Row(
                modifier = Modifier
                    .padding(end = 24.dp, top = 20.dp, bottom = 20.dp)
                    .clickable {
                        val path = viewModel.photoPath.value!!
                        val navigateFrom = detailViewModel.navigateFrom.value!!

                        when (navigateFrom) {
                            MainScreen.Detail -> {
                                val index = viewModel.indexForm.value!!
                                val id = detailViewModel.uiState.value.detailForm!!.fields[index].id
                                val value = detailViewModel.uiState.value.values[id] as ImageValue?
                                var filePath = value?.filePath?.toList() ?: listOf()


                                ArrayList(filePath)
                                    .apply { add(path) }
                                    .also { array -> filePath = array.toList() }

                                addPhotoToGallery(path)

                                detailViewModel.getPresignedUrl(ArrayList(filePath), index, 2)
                                viewModel.navigateTo(MainScreen.Detail)
                            }
                            MainScreen.Sketch -> {
                                val uri = Uri.fromFile(File(path))

                                detailViewModel.setCameraResultUri(uri)
                                viewModel.navigateTo(MainScreen.Sketch)
//                                dispatcher.onBackPressed()
                            }
                            else -> {

                            }
                        }

                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_confirm),
                    contentDescription = "Confirm photo",
                    tint = Color.White,
                )
                Text(
                    text = stringResource(id = R.string.done).uppercase(),
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 8.dp),
                    fontFamily = fontRoboto
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPhotoScreen() {
    val viewModel: CameraViewModel = hiltViewModel()
    val detailViewModel: DetailFormViewModel = hiltViewModel()
    WorxTheme {
        PhotoPreviewScreen(viewModel = viewModel, detailViewModel) {}
    }
}