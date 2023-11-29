package id.worx.mobile.screen.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import id.worx.mobile.viewmodel.DetailFormViewModel
import id.worx.mobile.viewmodel.EventStatus
import java.io.File


@Composable
fun WorxListDataView(
    filePath: List<String>,
    fileId: List<Int>,
    viewModel: DetailFormViewModel,
    typeDataView: Int =0,
    setComponentData: () -> Unit,
    filePathNotEmpty: (Int, String) -> Unit,
    fileIdNotEmpty: (Int) -> Unit
) {
    val formStatus = viewModel.uiState.collectAsState().value.status

    if (filePath.isNotEmpty()) {
        Column {
            filePath.forEachIndexed { index, item ->
                val file = File(item)
                Log.d("TAG", "WorxListDataView: $file")
                val fileSize = (file.length() / 1024).toInt()
                when (typeDataView) {
                    0 -> {
                        FileDataView(
                            filePath = item,
                            fileSize = fileSize,
                            onClick = {
                                filePathNotEmpty(index, item)
                                setComponentData()
                            })
                    }
                    1 -> {
                        ImageDataView(
                            filePath = item,
                            fileSize = fileSize,
                            onClick = {
                                filePathNotEmpty(index, item)
                                setComponentData()
                            })
                    }
                    else -> {}
                }
            }
        }
    } else if (fileId.isNotEmpty()) {
        fileId.forEach {
            when (typeDataView) {
                0 -> {
                    FileDataView(
                        filePath = "File $it",
                        fileSize = 0,
                        showCloseButton = !arrayListOf(
                            EventStatus.Done,
                            EventStatus.Submitted
                        ).contains(formStatus),
                        onClick = {
                            fileIdNotEmpty(it)
                            setComponentData()
                        }
                    )
                }
                1 -> {
                    ImageDataView(
                        filePath = "File $it",
                        fileSize = 0,
                        showCloseButton = !arrayListOf(
                            EventStatus.Done,
                            EventStatus.Submitted
                        ).contains(formStatus),
                        onClick = {
                            fileIdNotEmpty(it)
                            setComponentData()
                        }
                    )
                }
                else -> {}
            }
        }
    }
}