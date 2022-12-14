package id.worx.device.client.data.api

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.*
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

    suspend fun syncWithServer(
        dataToSync: Int,
        viewLifecycleOwner: LifecycleOwner,
    onSuccessCallback: () -> Unit){
        if (dataToSync == UPLOADTOSERVER) {
            uploadSubmissionWork()
        } else if (dataToSync == DOWNLOADFROMSERVER) {
            downloadFormTemplate(viewLifecycleOwner, onSuccessCallback)
            downloadSubmissionList(viewLifecycleOwner, onSuccessCallback)
        } else {
            downloadFormTemplate(viewLifecycleOwner, onSuccessCallback)
            downloadSubmissionList(viewLifecycleOwner, onSuccessCallback)
            uploadSubmissionWork()
        }
    }

    private val formTemplateWorkInfoItems: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData("form_template")

    private fun downloadFormTemplate(lifecycleOwner: LifecycleOwner, onSuccessCallback: () -> Unit) {
        val syncTemplateDBRequest = OneTimeWorkRequestBuilder<FormDownloadWorker>()
            .addTag("form_template")
            .setConstraints(networkConstraints)
            .build()

        workManager.enqueue(syncTemplateDBRequest)

        formTemplateWorkInfoItems.observe(lifecycleOwner) { list ->
            val workInfo = list[0]
            if (list.isNotEmpty() && workInfo.state.isFinished) {
                onSuccessCallback()
            }
        }
    }


    private suspend fun uploadSubmissionWork() {
            repository.getAllUnsubmitted().collect {

                val gson = GsonBuilder()
                    .registerTypeAdapter(Fields::class.java, FieldsDeserializer())
                    .registerTypeAdapter(Value::class.java, ValueSerialize())
                    .create()
                val uploadData: Data = workDataOf("submit_form_list" to gson.toJson(it))

                val uploadSubmisison = OneTimeWorkRequestBuilder<SubmissionUploadWorker>()
                    .setInputData(uploadData)
                    .addTag("upload_submission")
                    .setConstraints(networkConstraints)
                    .build()

                workManager.enqueue(uploadSubmisison)
            }
    }

    private val downloadSubmissionWorkInfoItems: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData("submission_list")

    private fun downloadSubmissionList(lifecycleOwner: LifecycleOwner, onSuccessCallback: () -> Unit) {
        val syncSubmitFormDBRequest = OneTimeWorkRequestBuilder<SubmissionDownloadWorker>()
            .addTag("submission_list")
            .setConstraints(networkConstraints)
            .build()

        workManager.enqueue(syncSubmitFormDBRequest)

        formTemplateWorkInfoItems.observe(lifecycleOwner, Observer { list ->
            val workInfo = list[0]
            if (list.isNotEmpty() && workInfo.state.isFinished) {
                onSuccessCallback()
            }
        })
    }

    companion object {
        const val UPLOADTOSERVER = 1
        const val DOWNLOADFROMSERVER = 2
    }
}