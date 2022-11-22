package id.worx.device.client.screen.components

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import id.worx.device.client.R
import id.worx.device.client.Util.getRealPathFromURI
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.FileField
import id.worx.device.client.model.FileValue
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.theme.textFormDescription
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus
import java.io.File

@Composable
fun WorxAttachFile(indexForm: Int, description: String, viewModel: DetailFormViewModel, session: Session) {
    val theme = session.theme
    val uiState = viewModel.uiState.collectAsState().value
    val form = uiState.detailForm!!.fields[indexForm] as FileField
    val fileValue = uiState.values[form.id] as FileValue?
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

    var context = LocalContext.current

    val launcherFile =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    it.data?.data?.let { uri ->
                        val path = getRealPathFromURI(context, uri)
                        filePath = ArrayList(filePath).apply { add(path) }.toList()
                        viewModel.getPresignedUrl(ArrayList(filePath), indexForm, 1)
                    }
                }
            })


    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            form.label ?: "",
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier.padding(start = 17.dp, bottom = 8.dp, end = 16.dp)
        )
        if (description.isNotBlank()) {
            Text(
                text = description,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier.padding(bottom = 8.dp, start = 17.dp)
            )
        }
        if (filePath.isNotEmpty()) {
            Column {
                filePath.forEachIndexed { index, item ->
                    val file = File(item)
                    val fileSize = (file.length() / 1024).toInt()
                    FileDataView(filePath = item, fileSize = fileSize) {
                        ArrayList(filePath).apply { remove(item) }.also { filePath = it.toList() }
                        ArrayList(fileIds).apply { removeAt(index) }.also { fileIds = it.toList() }
                        viewModel.setComponentData(
                            indexForm,
                            if (filePath.isEmpty()) {
                                null
                            } else {
                                FileValue(
                                    value = ArrayList(fileIds),
                                    filePath = ArrayList(filePath)
                                )
                            }
                        )
                    }
                }
            }
        } else if (fileIds.isNotEmpty()){
            Column {
                fileIds.forEach {
                    FileDataView(
                        filePath = "File $it",
                        fileSize = 0,
                        showCloseButton = !arrayListOf(EventStatus.Done, EventStatus.Submitted).contains(formStatus)
                    ) {
                        ArrayList(fileIds).apply { remove(it) }.also { ids -> fileIds = ids.toList() }
                        viewModel.setComponentData(
                            indexForm,
                            if (fileIds.isEmpty()) {
                                null
                            } else {
                                FileValue(value = ArrayList(fileIds), filePath = ArrayList(filePath))
                            }
                        )
                    }
                }
            }
        }
        if (arrayListOf(EventStatus.Loading, EventStatus.Filling, EventStatus.Saved).contains(formStatus)) {
            AttachFileButton((form.maxFiles ?: 10) > fileIds.size, launcherFile, theme = theme)
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun AttachFileButton(
    isMaxFilesNotAchieved:Boolean,
    launcherFile: ManagedActivityResultLauncher<Intent, ActivityResult>,
    theme: String?
) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
        .apply { type = "*/*" }

    val context = LocalContext.current

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launcherFile.launch(intent)
        } else {
            Toast.makeText(context, context.getString(R.string.permission_rejected), Toast.LENGTH_LONG).show()
        }
    }

    ActionRedButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        iconRes = R.drawable.ic_baseline_attach_file_24,
        title = stringResource(id = R.string.add_file),
        actionClick = {
            if (!isMaxFilesNotAchieved){
                Toast.makeText(context, context.getString(R.string.max_files_message), Toast.LENGTH_LONG).show()
            } else {
                if (android.os.Build.VERSION.SDK_INT > 32){
                    launcherFile.launch(intent)
                } else {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) -> {
                            launcherFile.launch(intent)
                        }
                        else -> {
                            launcherPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    }
                }
            }
        }, theme = theme
    )
}

@Composable
fun FileDataView(
    filePath: String,
    showCloseButton: Boolean = true,
    fileSize: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            modifier = Modifier.align(Alignment.CenterVertically),
            painter = painterResource(id = R.drawable.ic_file_gray),
            tint = Color.Black.copy(0.54f),
            contentDescription = "File Icon"
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f)
        ) {
            Text(text = filePath.substringAfterLast("/"), style = Typography.body2.copy(MaterialTheme.colors.onSecondary))
            if(fileSize > 0) Text(text = "$fileSize kb", style = Typography.body2.copy(MaterialTheme.colors.onSecondary))
        }
        if (showCloseButton) {
            Icon(
                modifier = Modifier
                    .padding(start = 12.dp, end = 4.dp)
                    .clickable { onClick() }
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.ic_delete_circle),
                tint = Color.Black.copy(0.54f),
                contentDescription = "Clear File"
            )
        }
    }
}