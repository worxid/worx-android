package id.worx.device.client.data.dao

import androidx.room.*
import id.worx.device.client.model.EmptyForm
import kotlinx.coroutines.flow.Flow

@Dao
interface FormDAO {
    @Transaction
    suspend fun deleteAndCreate(list: List<EmptyForm>) {
        deleteAllForm()
        insertAll(list)
    }

    @Query("SELECT * FROM form ORDER BY id ASC")
    fun getAllForm(): Flow<List<EmptyForm>>

    @Query("SELECT * FROM form WHERE id IN (:id)")
    suspend fun loadFormById(id:Int): List<EmptyForm>

    @Delete
    fun deleteForm(form: EmptyForm)

    @Query("DELETE FROM form")
    fun deleteAllForm()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateForm(form: EmptyForm)

    @Insert
    fun insertAll(templateList: List<EmptyForm>)

}