package id.worx.device.client.data.database

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.EntryPointAccessors
import id.worx.device.client.data.api.FieldsDeserializer
import id.worx.device.client.data.api.ValueSerialize
import id.worx.device.client.model.Fields
import id.worx.device.client.model.SubmitForm
import id.worx.device.client.model.Value
import id.worx.device.client.repository.SourceDataRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmissionUploadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var repo: SourceDataRepository

    init {
        val repoFactory = EntryPointAccessors.fromApplication(
            appContext,
            FormRepoEntryPoint::class.java
        )
        repo = repoFactory.formRepo
    }

    override suspend fun doWork(): Result {
        val rawdata = inputData.getString("submit_form_list")
        val gson = GsonBuilder()
            .registerTypeAdapter(Fields::class.java, FieldsDeserializer())
            .registerTypeAdapter(Value::class.java, ValueSerialize())
            .create()
        val submissionList =
            gson.fromJson<List<SubmitForm>>(rawdata, object : TypeToken<List<SubmitForm>>() {}.type)

        try {
            if (!submissionList.isNullOrEmpty()) {
                val submission = submissionList[0]
                return if (submitForm(submission)) {
                    Result.success()
                } else {
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            return Result.retry()
        }
        return Result.success()
    }

    private suspend fun submitForm(form: SubmitForm): Boolean {
        val res = CompletableDeferred<Boolean>()

        val response = repo.submitForm(form)
        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {
                repo.insertOrUpdateSubmitForm(form.copy(status = 2))
                res.complete(true)
            }
        }
        return res.await()
    }
}