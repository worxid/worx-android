package id.worx.device.client.screen.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.util.getRealPathFromURI
import id.worx.device.client.Util
import id.worx.device.client.data.database.Session
import id.worx.device.client.model.Fields
import id.worx.device.client.model.fieldmodel.FileValue
import id.worx.device.client.model.fieldmodel.ImageValue
import id.worx.device.client.viewmodel.DetailFormViewModel
import id.worx.device.client.viewmodel.EventStatus

@Composable
fun WorxBaseAttach(
    indexForm: Int,
    viewModel: DetailFormViewModel,
    session: Session,
    typeValue: Int,
    validation: Boolean = false,
    content: @Composable (List<Int>, String, Fields, ManagedActivityResultLauncher<Intent, ActivityResult>) -> Unit
) {
    val theme = session.theme
    val uiState = viewModel.uiState.collectAsState().value
    val form = uiState.detailForm!!.fields[indexForm]
    val fileValue = uiState.values[form.id]
    var filePath by if (fileValue != null) {
        remember {
            mutableStateOf(
                if (typeValue == 1) (fileValue as FileValue).filePath.toList() else (fileValue as ImageValue).filePath.toList()
            )
        }
    } else {
        remember {
            mutableStateOf(listOf())
        }
    }
    val warningInfo =
        if (form.required == true && filePath.isEmpty()) "${form.label} is required" else ""
    var fileIds by if (fileValue != null) {
        remember {
            mutableStateOf(
                if (typeValue == 1) (fileValue as FileValue).value.toList() else (fileValue as ImageValue).value.toList()
            )
        }
    } else {
        remember {
            mutableStateOf(listOf())
        }
    }
    val formStatus = viewModel.uiState.collectAsState().value.status
    val context = LocalContext.current

    val launcher=
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                if (it.resultCode == Activity.RESULT_OK &&
                    it.data != null
                ) {
                    var path: String? = null
                    when (typeValue) {
                        1 -> {
                            it.data?.data?.let { uri ->
                                path = Util.getRealPathFromURI(context, uri)
                            }
                        }
                        2 -> {
                            it.data!!.getParcelableArrayListExtra<Uri>(FishBun.INTENT_PATH)
                                ?.forEach { uri -> path = getRealPathFromURI(context, uri) }
                        }
                    }
                    filePath = ArrayList(filePath).apply { add(path) }.toList()
                    viewModel.getPresignedUrl(ArrayList(filePath), indexForm, typeValue)
                }
            }
        )

    WorxBaseField(
        indexForm = indexForm,
        viewModel = viewModel,
        validation = validation,
        warningInfo = warningInfo
    ) {
        WorxListDataView(
            filePath = filePath,
            fileId = fileIds,
            typeDataView = 1,
            viewModel = viewModel,
            setComponentData = {
                viewModel.setComponentData(
                    indexForm, if (filePath.isEmpty()) {
                        null
                    } else {
                        ImageValue(
                            value = ArrayList(fileIds),
                            filePath = ArrayList(filePath)
                        )
                    }
                )
            },
            filePathNotEmpty = { index, item ->
                ArrayList(filePath).apply { remove(item) }.also { filePath = it.toList() }
                ArrayList(fileIds).apply { remove(index) }.also { fileIds = it.toList() }
            },
            fileIdNotEmpty = {
                ArrayList(fileIds).apply { remove(it) }.also { ids -> fileIds = ids.toList() }
            }
        )
        if (arrayListOf(
                EventStatus.Loading,
                EventStatus.Filling,
                EventStatus.Saved,
            ).contains(formStatus)
        ) {
            if (theme != null) {
                content(fileIds, theme, form, launcher)
            }
        }
    }
}