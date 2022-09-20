package id.worx.device.client.data.dao

import androidx.room.*
import id.worx.device.client.model.SubmitForm
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDAO {
    @Query("SELECT * FROM draft ORDER BY id ASC")
    fun getAllDraft(): Flow<List<SubmitForm>>

    @Query("SELECT * FROM draft WHERE id IN (:id)")
    fun loadDraftById(id:Int): List<SubmitForm>

    @Delete
    fun deleteDraft(form: SubmitForm)

    @Query("DELETE FROM draft")
    fun deleteAllDraft()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDraft(form: SubmitForm)

}