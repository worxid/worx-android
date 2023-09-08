package id.worx.device.client.data.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import id.worx.device.client.model.SubmitForm
import kotlinx.coroutines.flow.Flow

@Dao
interface SubmitFormDAO {

    @RawQuery(observedEntities = [SubmitForm::class])
    suspend fun getSubmitForm(query: SupportSQLiteQuery): List<SubmitForm>

    @Query("SELECT * FROM submit_form WHERE status = 1")
    fun getAllUnsubmitted(): Flow<List<SubmitForm>>

    @Query("DELETE FROM submit_form WHERE dbId = :id")
    suspend fun deleteSubmitForm(id: Int)

    @Query("SELECT * FROM submit_form WHERE dbId = :id")
    suspend fun loadSubmitFormById(id:Int): List<SubmitForm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateForm(form: SubmitForm)

    @Query("DELETE FROM submit_form WHERE status = 2")
    suspend fun deleteExistingApiSubmission()

    @Insert
    fun insertAll(submissionList: List<SubmitForm>)

    @Transaction
    suspend fun addSubmissionFromApi(list: List<SubmitForm>) {
        deleteExistingApiSubmission()
        insertAll(list)
    }

}