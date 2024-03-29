package id.worx.mobile.screen.main

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import id.worx.mobile.MainScreen
import id.worx.mobile.R
import id.worx.mobile.model.fieldmodel.ImageValue
import id.worx.mobile.theme.MineShaft
import id.worx.mobile.theme.WorxTheme
import id.worx.mobile.theme.fontRoboto
import id.worx.mobile.viewmodel.CameraViewModel
import id.worx.mobile.viewmodel.DetailFormViewModel
import java.io.File

@Composable
fun PhotoPreviewScreen(
    viewModel: CameraViewModel,
    detailViewModel: DetailFormViewModel,
    addPhotoToGallery: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .background(MineShaft)
                .fillMaxWidth()
                .height(82.dp)
        )

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
                .background(MineShaft)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, top = 20.dp, bottom = 20.dp)
                    .clickable { viewModel.rejectPhoto() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.retake),
                    fontSize = 16.sp,
                    color = Color.White,
                    fontFamily = fontRoboto,
                )
            }
            Row(
                modifier = Modifier
                    .padding(end = 16.dp, top = 20.dp, bottom = 20.dp)
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
                Text(
                    text = stringResource(id = R.string.use_photo),
                    fontSize = 16.sp,
                    color = Color.White,
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