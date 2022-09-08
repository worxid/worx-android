package id.worx.device.client.screen.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import id.worx.device.client.R
import id.worx.device.client.screen.ActionRedButton
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorxAttachImage(indexForm:Int, viewModel:DetailFormViewModel, navigateToPhotoCamera: () -> Unit) {
    val filePath = viewModel.uiState.detailForm?.componentList?.get(indexForm)?.Outputdata ?: ""
    var showImageDataView by remember { mutableStateOf(false) }

    val launcherGallery =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.path?.let { path -> viewModel.setComponentData(indexForm, path) }
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            viewModel.uiState.detailForm!!.componentList[indexForm].inputData.title,
            style = Typography.body2,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        )
        if (showImageDataView && filePath.isNotEmpty()) {
            val file = File(filePath)
            val fileSize = (file.length() / 1024).toInt()
            ImageDataView(filePath = filePath, fileSize = fileSize) {
                viewModel.setComponentData(indexForm, "")
            }
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TakeImageButton(navigateToPhotoCamera)
            GalleryImageButton(launcherGallery = launcherGallery)
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun ImageDataView(
    filePath: String,
    fileSize: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        AsyncImage(
            model = File(filePath),
            contentDescription = "Image",
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f)
        ) {
            Text(text = filePath, style = Typography.body2)
            Text(text = "$fileSize kb", style = Typography.body2)
        }
        Icon(
            modifier = Modifier
                .padding(start = 12.dp, end = 4.dp)
                .clickable { onClick() }
                .align(Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.ic_delete_circle),
            contentDescription = "Clear File"
        )
    }
}

@Composable
private fun TakeImageButton(
    navigateToPhotoCamera: () -> Unit
) {
    val context = LocalContext.current

    val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.containsValue(false)) {
            Toast.makeText(context, "Permission is denied", Toast.LENGTH_LONG).show()
        } else {
            navigateToPhotoCamera()
        }
    }

    ActionRedButton(
        modifier = Modifier.padding(0.dp),
        iconRes = R.drawable.ic_photo_camera,
        title = "Camera",
        actionClick = {
            if (
                requiredPermissions.all {
                    ContextCompat.checkSelfPermission(
                        context,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }) {
                navigateToPhotoCamera()
            } else {
                launcherPermission.launch(requiredPermissions)
            }
        })
}

private fun createImageFile(
    context: Context,
    savePhotoPath: (String) -> Unit
): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.externalMediaDirs.firstOrNull()?.let {
        File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        savePhotoPath(absolutePath)
    }
}

@Composable
private fun GalleryImageButton(
    launcherGallery: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
        .apply { type = "image/*" }

    val context = LocalContext.current

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launcherGallery.launch(intent)
        } else {
            Toast.makeText(context, "Permission is denied", Toast.LENGTH_LONG).show()
        }
    }

    ActionRedButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        iconRes = R.drawable.ic_image,
        title = stringResource(R.string.gallery),
        actionClick = {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) -> {
                    launcherGallery.launch(intent)
                }
                else -> {
                    launcherPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        })
}