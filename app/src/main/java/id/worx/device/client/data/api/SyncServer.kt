package id.worx.device.client.data.api

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.GsonBuilder
import id.worx.device.client.WorxApplication
import id.worx.device.client.data.database.FormDownloadWorker
import id.worx.device.client.data.database.SubmissionDownloadWorker
import id.worx.device.client.data.database.SubmissionUploadWorker
import id.worx.device.client.model.Fields
import id.worx.device.client.model.Value
import id.worx.device.client.repository.SourceDataRepository
import javax.inject.Inject

class SyncServer @Inject constructor(
    private val repository: SourceDataRepository
) {

    private var workManager = WorkManager.getInstance(WorxApplication())
    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    suspend fun syncWithServer(dataToSync: Int) {
        when (dataToSync) {
            UPLOADTOSERVER -> {
                uploadSubmissionWork()
            }

            DOWNLOADFROMSERVER -> {
                downloadForms()
            }

            else -> {
                downloadForms()
                uploadSubmissionWork()
            }
        }
    }

    private suspend fun downloadForms() {
        val syncTemplateDBRequest = OneTimeWorkRequestBuilder<FormDownloadWorker>()
            .addTag("form_template")
            .setConstraints(networkConstraints)
            .build()

        val syncSubmitFormDBRequest = OneTimeWorkRequestBuilder<SubmissionDownloadWorker>()
            .addTag("submission_list")
            .setConstraints(networkConstraints)
            .build()

        workManager.beginUniqueWork(
            "download_form",
            ExistingWorkPolicy.REPLACE,
            listOf(syncTemplateDBRequest, syncSubmitFormDBRequest)
        ).enqueue()
    }

    private suspend fun uploadSubmissionWork() {
        val unSubmittedList = repository.getAllUnsubmitted()

        val gson = GsonBuilder()
            .registerTypeAdapter(Fields::class.java, FieldsDeserializer())
            .registerTypeAdapter(Value::class.java, ValueSerialize())
            .create()
        val uploadData: Data = workDataOf("submit_form_list" to gson.toJson(unSubmittedList))

        val uploadSubmisison = OneTimeWorkRequestBuilder<SubmissionUploadWorker>()
            .setInputData(uploadData)
            .addTag("upload_submission")
            .setConstraints(networkConstraints)
            .build()

        workManager.enqueue(uploadSubmisison)
    }

    companion object {
        const val UPLOADTOSERVER = 1
        const val DOWNLOADFROMSERVER = 2
    }
}