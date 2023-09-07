package id.worx.device.client.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import id.worx.device.client.model.DraftForm
import kotlinx.coroutines.flow.Flow

@Dao
interface DraftDAO {

    @RawQuery(observedEntities = [DraftForm::class])
    fun getSortedDraftForms(query: SupportSQLiteQuery): Flow<List<DraftForm>>

    @Query("SELECT * FROM draft_form WHERE status IN (1,2)")
    fun getDraftSubmission(): Flow<List<DraftForm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateForm(form: DraftForm)

    @Query("DELETE FROM draft_form WHERE dbId = :id")
    suspend fun deleteDraftForm(id: Int)
}