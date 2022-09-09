package id.worx.device.client.screen.components

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.ui.album.ui.AlbumActivity
import com.sangcomz.fishbun.util.getRealPathFromURI
import id.worx.device.client.R
import id.worx.device.client.screen.ActionRedButton
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import java.io.File

@Composable
fun WorxAttachImage(indexForm:Int, viewModel:DetailFormViewModel, setIndexData: () -> Unit, navigateToPhotoCamera: () -> Unit) {
    val form = viewModel.uiState.detailForm!!.componentList[indexForm]
    val title = form.inputData.title

    val filePath = if (form.Outputdata != ""){
        remember{ mutableStateOf<String?>(form.Outputdata)}
    } else {
        remember{ mutableStateOf<String?>(null)}
    }

    val context = LocalContext.current

    val launcherGallery =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.getParcelableArrayListExtra<Uri>(FishBun.INTENT_PATH)?.forEach { uri ->
                val fPath = getRealPathFromURI(context, uri)
                Log.d("data", fPath)
                viewModel.setComponentData(indexForm, uri.toString())
                filePath.value = fPath
            }
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = Typography.body2,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        )
        if (!filePath.value.isNullOrEmpty()) {
            val file = File(filePath.value ?: "")
            val fileSize = (file.length() / 1024).toInt()
            ImageDataView(filePath = filePath.value ?: "", fileSize = fileSize) {
                viewModel.setComponentData(indexForm, "")
                filePath.value = null
            }
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TakeImageButton(navigateToPhotoCamera, setIndexData)
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
            model = filePath,
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
    navigateToPhotoCamera: () -> Unit,
    sendIndexFormData: () -> Unit
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
                sendIndexFormData()
            } else {
                launcherPermission.launch(requiredPermissions)
            }
        })
}

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@Composable
private fun GalleryImageButton(
    launcherGallery: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val context = LocalContext.current

    val requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.containsValue(false)) {
            Toast.makeText(context, "Permission is denied", Toast.LENGTH_LONG).show()
        } else {
            launcherGallery.launch(Intent(context, AlbumActivity::class.java))
        }
    }

    ActionRedButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        iconRes = R.drawable.ic_image,
        title = stringResource(R.string.gallery),
        actionClick = {
            if (
                requiredPermissions.all {
                    ContextCompat.checkSelfPermission(
                        context,
                        it
                    ) == PackageManager.PERMISSION_GRANTED
                }) {
                launcherGallery.launch(Intent(context, AlbumActivity::class.java))
            } else {
                launcherPermission.launch(requiredPermissions)
            }
        })
}