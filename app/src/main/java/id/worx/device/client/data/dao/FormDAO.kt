package id.worx.device.client.data.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import id.worx.device.client.model.EmptyForm

@Dao
interface FormDAO {
    @Transaction
    suspend fun deleteAndCreate(list: List<EmptyForm>) {
        deleteAllForm()
        insertAll(list)
    }

    @RawQuery(observedEntities = [EmptyForm::class])
    suspend fun getSortedAllForms(query: SupportSQLiteQuery): List<EmptyForm>

    @Query("SELECT * FROM form ORDER BY id ASC")
    suspend fun getAllForm(): List<EmptyForm>

    @Query("SELECT * FROM form WHERE id IN (:id)")
    suspend fun loadFormById(id:Int): List<EmptyForm>

    @Delete
    fun deleteForm(form: EmptyForm)

    @Query("DELETE FROM form")
    fun deleteAllForm()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateForm(form: EmptyForm)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(templateList: List<EmptyForm>)

}