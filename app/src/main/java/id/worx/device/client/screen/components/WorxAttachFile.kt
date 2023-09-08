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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import id.worx.device.client.R
import id.worx.device.client.Util.getRealPathFromURI
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.fieldmodel.FileField
import id.worx.device.client.model.fieldmodel.FileValue
import id.worx.device.client.model.fieldmodel.ImageField
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxAttachFile(
    indexForm: Int,
    viewModel: DetailFormViewModel,
    session: Session,
    validation: Boolean = false
) {
    WorxBaseAttach(
        indexForm = indexForm,
        viewModel = viewModel,
        typeValue = 1,
        session = session,
        validation = validation
    ) { fileIds, theme, forms, launcher ->
        val form = forms as FileField
        AttachFileButton(
            Modifier.padding(horizontal = 16.dp),
            (form.maxFiles ?: 10) > fileIds.size,
            launcher,
            theme = theme
        )
    }
}

@Composable
private fun AttachFileButton(
    modifier: Modifier,
    isMaxFilesNotAchieved: Boolean,
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
            Toast.makeText(
                context,
                context.getString(R.string.permission_rejected),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    ActionRedButton(
        modifier = modifier,
        iconRes = R.drawable.ic_clip,
        title = stringResource(id = R.string.add_file),
        actionClick = {
            if (!isMaxFilesNotAchieved) {
                Toast.makeText(
                    context,
                    context.getString(R.string.max_files_message),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (android.os.Build.VERSION.SDK_INT > 32) {
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
        }
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(2.dp))
                .background(WorxCustomColorsPalette.current.documentBackground)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_document),
                tint = WorxCustomColorsPalette.current.textFieldIcon,
                contentDescription = "File Icon"
            )
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f),
        ) {
            Text(
                text = filePath.substringAfterLast("/"),
                style = Typography.body2.copy(WorxCustomColorsPalette.current.text)
            )
            if (fileSize > 0) Text(
                text = "$fileSize kb",
                style = Typography.body2.copy(WorxCustomColorsPalette.current.subText)
            )
        }
        if (showCloseButton) {
            Icon(
                modifier = Modifier
                    .padding(start = 12.dp, end = 4.dp)
                    .clickable { onClick() }
                    .align(Alignment.CenterVertically),
                painter = painterResource(id = R.drawable.ic_delete_circle),
                tint = WorxCustomColorsPalette.current.textFieldIcon,
                contentDescription = "Clear File"
            )
        }
    }
}