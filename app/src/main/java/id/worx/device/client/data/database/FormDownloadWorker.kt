package id.worx.device.client.data.database

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import id.worx.device.client.repository.SourceDataRepository
import id.worx.device.client.repository.SourceDataRepository.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@EntryPoint
@InstallIn(SingletonComponent::class)
@Named("form_template_repo")
interface FormRepoEntryPoint {
    val formRepo: SourceDataRepository
}

@Singleton
class FormDownloadWorker(appContext: Context, workerParams: WorkerParameters) :
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
        return withContext(Dispatchers.IO) {
            try {
                fetchFormTemplate(repo)
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }

    private suspend fun fetchFormTemplate(repo: SourceDataRepository) {
        val response = repo.fetchAllForm()
        if (response.isSuccessful) {
            val list = response.body()?.list
            if (list != null) {
                repo.addAllFormTemplate(list)
            }
        } else {
            val errorMessage = "Error " + response.code().toString()
            Log.d(TAG, errorMessage)
        }
    }
}