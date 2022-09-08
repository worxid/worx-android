package id.worx.device.client.screen.components

import android.Manifest
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import id.worx.device.client.R
import id.worx.device.client.screen.ActionRedButton
import id.worx.device.client.theme.GrayDivider
import id.worx.device.client.theme.Typography
import id.worx.device.client.viewmodel.DetailFormViewModel
import java.io.File

@Composable
fun WorxAttachFile(indexForm:Int, viewModel: DetailFormViewModel) {
    val filePath = viewModel.uiState.detailForm?.componentList?.get(indexForm)?.Outputdata ?: ""

    val launcherFile =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            it.data?.data?.path?.let { path -> viewModel.setComponentData(indexForm, path) }
        }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            viewModel.uiState.detailForm!!.componentList[indexForm].inputData.title,
            style = Typography.body2,
            modifier = Modifier.padding(start = 17.dp, bottom = 8.dp, end = 16.dp)
        )
        if (filePath.isNotEmpty()) {
            val file = File(filePath)
            val fileSize = (file.length() / 1024).toInt()
            FileDataView(filePath = filePath, fileSize = fileSize) { viewModel.setComponentData(indexForm, "") }
        }
        AttachFileButton(launcherFile)
        Divider(color = GrayDivider, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun AttachFileButton(
    launcherFile: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
        .apply { type = "file/*" }

    val context = LocalContext.current

    val launcherPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            launcherFile.launch(intent)
        } else {
            Toast.makeText(context, "Permission is denied", Toast.LENGTH_LONG).show()
        }
    }

    ActionRedButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        iconRes = R.drawable.ic_baseline_attach_file_24,
        title = stringResource(id = R.string.add_file),
        actionClick = {
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
        })
}

@Composable
private fun FileDataView(
    filePath: String,
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
            contentDescription = "File Icon"
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