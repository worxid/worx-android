package id.worx.device.client.screen.components

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.util.getRealPathFromURI
import id.worx.device.client.R
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.SketchField
import id.worx.device.client.model.SketchValue
import id.worx.device.client.screen.main.SettingTheme
import id.worx.device.client.theme.*
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus


@Composable
fun WorxSketch(
    modifier: Modifier = Modifier,
    indexForm: Int,
    viewModel: DetailFormViewModel,
    session: Session,
    validation: Boolean
) {
    val context = LocalContext.current

    val state = viewModel.uiState.collectAsState()

    val form = state.value.detailForm!!.fields[indexForm] as SketchField
    val theme = session.theme

    val value = state.value.values[form.id] as SketchValue?

    val bitmap = if (value == null) {
        remember {
            mutableStateOf<Bitmap?>(null)
        }
    } else {
        remember {
            mutableStateOf(value.bitmap)
        }
    }

    val warningInfo =
        if (form.required == true && bitmap.value == null) "${form.label} is required" else ""

    val fileId = value?.value
    val formStatus = state.value.status

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        Text(
            form.label ?: "Draw Sketch",
            style = Typography.body2.copy(MaterialTheme.colors.onSecondary),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (!form.description.isNullOrEmpty()) {
            Text(
                text = form.description!!,
                color = if (theme == SettingTheme.Dark) textFormDescriptionDark else textFormDescription,
                style = MaterialTheme.typography.body1.copy(textFormDescription),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // add condition to check bitmap
        if (value?.bitmap != null) {
            SketchResultView(bitmap = bitmap.value) {
                viewModel.setComponentData(indexForm, null)
                bitmap.value = null
            }
        } else if (fileId != null) {
            FileDataView(
                filePath = "File $fileId",
                fileSize = 0,
                showCloseButton = !arrayListOf(
                    EventStatus.Done, EventStatus.Submitted
                ).contains(
                    formStatus
                )
            ) {}
        } else {
            if (arrayListOf(EventStatus.Loading, EventStatus.Filling, EventStatus.Saved).contains(
                    formStatus
                )
            ) {
                ActionRedButton(
                    modifier = Modifier,
                    iconRes = R.drawable.ic_line_thick,
                    title = stringResource(id = R.string.draw_sketch),
                    theme = theme
                ) {
                    viewModel.goToSketch(indexForm)
                }
            }
        }

        if (warningInfo.isNotBlank()) {
            if (validation) {
                Text(
                    text = warningInfo,
                    modifier = Modifier
                        .padding(top = 4.dp),
                    color = PrimaryMain
                )
            }
            form.isValid = false
        } else {
            form.isValid = true
        }
        Divider(color = GrayDivider, modifier = Modifier.padding(vertical = 16.dp))
    }
}

@Composable
private fun SketchResultView(
    bitmap: Bitmap?,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
        ) {
            AsyncImage(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp),
                model = bitmap,
                contentScale = ContentScale.Fit,
                contentDescription = "Sketch",
            )
        }
        Icon(
            modifier = Modifier
                .padding(start = 30.dp, end = 4.dp)
                .clickable { onDelete() }
                .align(Alignment.Top),
            painter = painterResource(id = R.drawable.ic_delete_circle),
            contentDescription = "Clear File",
            tint = MaterialTheme.colors.onSecondary
        )
    }
}
