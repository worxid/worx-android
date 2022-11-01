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
import androidx.compose.material.Divider
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
import id.worx.device.client.model.ImageField
import id.worx.device.client.model.ImageValue
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.PrimaryMain
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.WorxTheme
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus
import java.io.File

@Composable
fun WorxAttachImage(
    indexForm: Int,
    viewModel: DetailFormViewModel,
    session: Session,
    setIndexData: () -> Unit,
    navigateToPhotoCamera: () -> Unit
) {
    val form = viewModel.uiState.collectAsState().value.detailForm!!.fields[indexForm] as ImageField
    val title = form.label ?: ""
    val theme = session.theme

    val fileValue = viewModel.uiState.collectAsState().value.values[form.id] as ImageValue?
    var filePath by if (fileValue != null) {
        remember { mutableStateOf(fileValue.filePath.toList()) }
    } else {
        remember {
            mutableStateOf(listOf())
        }
    }

    var fileIds by if (fileValue != null) {
        remember { mutableStateOf(fileValue.value.toList()) }
    } else {
        remember {
            mutableStateOf(listOf())
        }
    }

    val formStatus = viewModel.uiState.collectAsState().value.status

    val context = LocalContext.current

    val launcherGallery =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK &&
                    it.data != null) {
                    it.data!!.getParcelableArrayListExtra<Uri>(FishBun.INTENT_PATH)
                        ?.forEach { uri ->
                            val fPath = getRealPathFromURI(context, uri)
                            ArrayList(filePath).apply { add(fPath) }.also { array -> filePath = array.toList() }
                            viewModel.getPresignedUrl(ArrayList(filePath), indexForm, 2)
                        }
                }
            })

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title,
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        )
        if (filePath.isNotEmpty()) {
            Column {
                filePath.forEachIndexed { index, item ->
                    val file = File(item)
                    val fileSize = (file.length() / 1024).toInt()
                    ImageDataView(filePath = item, fileSize = fileSize) {
                        ArrayList(filePath).apply { remove(item) }.also { filePath = it.toList() }
                        ArrayList(fileIds).apply { remove(index) }.also { fileIds = it.toList() }
                        viewModel.setComponentData(
                            indexForm,
                            ImageValue(value = ArrayList(fileIds), filePath = ArrayList(filePath))
                        )
                    }
                }
            }
        } else if (fileIds.isNotEmpty()) {
            fileIds.forEach {
                ImageDataView(
                    filePath = "File $it",
                    fileSize = 0,
                    showCloseButton = !arrayListOf(
                        EventStatus.Done,
                        EventStatus.Submitted
                    ).contains(formStatus)
                ) {
                    ArrayList(fileIds).apply { remove(it) }.also { ids -> fileIds = ids.toList() }
                    viewModel.setComponentData(
                        indexForm,
                        ImageValue(value = ArrayList(fileIds), filePath = ArrayList(filePath))
                    )
                }
            }
        }
        if (arrayListOf(
                EventStatus.Loading,
                EventStatus.Filling,
                EventStatus.Saved
            ).contains(formStatus)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TakeImageButton((form.maxFiles ?: 10) > fileIds.size, navigateToPhotoCamera, setIndexData, theme)
                GalleryImageButton((form.maxFiles ?: 10) > fileIds.size, launcherGallery = launcherGallery, theme)
            }
            Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
private fun ImageDataView(
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
            Text(text = filePath.substringAfterLast("/"), style = Typography.body2.copy(MaterialTheme.colors.onSecondary))
            Text(text = "$fileSize kb", style = Typography.body2.copy(MaterialTheme.colors.onSecondary))
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
            if (!isMaxFilesNumberNotAchieved){
                Toast.makeText(context, "Already achieved max files. Please remove one of the them first", Toast.LENGTH_LONG).show()
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
            FishBun.with(context.getActivity()!!)
                .setImageAdapter(CoilAdapter())
                .setMaxCount(1)
                .setThemeColor(PrimaryMain.toArgb())
                .startAlbumWithActivityResultCallback(launcherGallery)
        }
    }

    ActionRedButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        iconRes = R.drawable.ic_image,
        title = stringResource(R.string.gallery),
        actionClick = {
            if (!isMaxFilesNumberNotAchieved){
                Toast.makeText(context, "Already achieved max files. Please remove one of the them first", Toast.LENGTH_LONG).show()
            }else if (
                requiredPermissions.all {
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
        }, theme = theme
    )
}

@Preview
@Composable
fun PreviewImageWorx(){
    val viewModel: DetailFormViewModel = hiltViewModel()
    WorxTheme() {
        WorxAttachImage(
            indexForm = 0 ,
            viewModel = viewModel,
            session = Session(LocalContext.current),
            setIndexData = {}) {
        }
    }
}