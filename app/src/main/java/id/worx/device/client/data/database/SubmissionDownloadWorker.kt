package id.worx.device.client.data.database

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.EntryPointAccessors
import id.worx.device.client.repository.SourceDataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmissionDownloadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    @Inject lateinit var repo: SourceDataRepository

    init {
        val repoFactory = EntryPointAccessors.fromApplication(
            appContext,
            FormRepoEntryPoint::class.java
        )
        repo = repoFactory.formRepo
    }

    override suspend fun doWork(): Result {
        return try {
            fetchSubmission(repo)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun fetchSubmission(repo: SourceDataRepository) {
        val response = repo.fetchAllSubmission()
        if (response.isSuccessful) {
            val list = response.body()?.list
            if (list != null) {
                repo.addSubmissionFromApiToDb(list)
            }
        } else {
            val errorMessage = "Error " + response.code().toString()
            Log.d(SourceDataRepository.TAG, errorMessage)
        }
    }
}