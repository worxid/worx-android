package id.worx.device.client.screen.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.CoilAdapter
import com.sangcomz.fishbun.util.getRealPathFromURI
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.fieldmodel.ImageField
import id.worx.device.client.model.fieldmodel.ImageValue
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxAttachImage(
    indexForm: Int,
    viewModel: DetailFormViewModel,
    session: Session,
    setIndexData: () -> Unit,
    validation: Boolean = false,
    navigateToPhotoCamera: () -> Unit,
) {
    WorxBaseAttach(
        indexForm = indexForm,
        viewModel = viewModel,
        typeValue = 2,
        session = session,
        validation = validation
    ) { fileIds, theme, forms, launcher ->
        val form = forms as ImageField
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TakeImageButton(
                (form.maxFiles ?: 10) > fileIds.size,
                navigateToPhotoCamera,
                setIndexData,
                theme
            )
            GalleryImageButton(
                (form.maxFiles ?: 10) > fileIds.size,
                launcherGallery = launcher,
                theme
            )
        }
    }
}

@Composable
fun ImageDataView(
    filePath: String,
    fileSize: Int,
    showCloseButton: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        AsyncImage(
            model = if (filePath.contains("File")) {
                android.R.drawable.ic_menu_gallery
            } else {
                filePath
            },
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
            Text(
                text = filePath.substringAfterLast("/"),
                style = Typography.body2.copy(MaterialTheme.colors.onSecondary)
            )
            if (fileSize > 0) Text(
                text = "$fileSize kb",
                style = Typography.body2.copy(MaterialTheme.colors.onSecondary)
            )
        }
        if (showCloseButton) {
            Icon(
                modifier = Modifier
                    .padding(start = 12.dp, end = 4.dp)
                    .clickable { onClick() }
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.ic_delete_circle),
                contentDescription = "Clear File",
                tint = MaterialTheme.colors.onSecondary
            )
        }
    }
}

@Composable
private fun TakeImageButton(
    isMaxFilesNumberNotAchieved: Boolean,
    navigateToPhotoCamera: () -> Unit,
    sendIndexFormData: () -> Unit,
    theme: String?
) {
    val context = LocalContext.current

    var requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    if (android.os.Build.VERSION.SDK_INT > 32) {
        requiredPermissions = arrayOf(Manifest.permission.CAMERA)
    }

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.containsValue(false)) {
            Toast.makeText(
                context,
                context.getString(R.string.permission_rejected),
                Toast.LENGTH_LONG
            ).show()
        } else {
            sendIndexFormData()
            navigateToPhotoCamera()
        }
    }

    ActionRedButton(
        modifier = Modifier.padding(0.dp),
        iconRes = R.drawable.ic_camera,
        title = "Camera",
        actionClick = {
            if (!isMaxFilesNumberNotAchieved) {
                Toast.makeText(
                    context,
                    context.getString(R.string.max_files_message),
                    Toast.LENGTH_LONG
                ).show()
            } else if (
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
        }, theme = theme
    )
}

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

@Composable
private fun GalleryImageButton(
    isMaxFilesNumberNotAchieved: Boolean,
    launcherGallery: ManagedActivityResultLauncher<Intent, ActivityResult>,
    theme: String?
) {
    val context = LocalContext.current

    var requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.containsValue(false)) {
            Toast.makeText(
                context,
                context.getString(R.string.permission_rejected),
                Toast.LENGTH_LONG
            ).show()
        } else {
            FishBun.with(context.getActivity()!!)
                .setImageAdapter(CoilAdapter())
                .setMaxCount(1)
                .setThemeColor(PrimaryMain.toArgb())
                .startAlbumWithActivityResultCallback(launcherGallery)
        }
    }

    ActionRedButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        iconRes = R.drawable.ic_gallery,
        title = stringResource(R.string.gallery),
        actionClick = {
            if (!isMaxFilesNumberNotAchieved) {
                Toast.makeText(
                    context,
                    context.getString(R.string.max_files_message),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (android.os.Build.VERSION.SDK_INT > 32) {
                    FishBun.with(context.getActivity()!!)
                        .setImageAdapter(CoilAdapter())
                        .setMaxCount(1)
                        .setThemeColor(PrimaryMain.toArgb())
                        .startAlbumWithActivityResultCallback(launcherGallery)
                } else {
                    if (requiredPermissions.all {
                            ContextCompat.checkSelfPermission(
                                context,
                                it
                            ) == PackageManager.PERMISSION_GRANTED
                        }) {
                        FishBun.with(context.getActivity()!!)
                            .setImageAdapter(CoilAdapter())
                            .setMaxCount(1)
                            .setThemeColor(PrimaryMain.toArgb())
                            .startAlbumWithActivityResultCallback(launcherGallery)
                    } else {
                        launcherPermission.launch(requiredPermissions)
                    }
                }
            }
        }, theme = theme
    )
}

@Preview
@Composable
fun PreviewImageWorx() {
    val viewModel: DetailFormViewModel = hiltViewModel()
    WorxTheme() {
        WorxAttachImage(
            indexForm = 0,
            viewModel = viewModel,
            session = Session(LocalContext.current),
            setIndexData = {}) {
        }
    }
}