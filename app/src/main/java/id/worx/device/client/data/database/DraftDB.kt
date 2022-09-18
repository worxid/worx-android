package id.worx.device.client.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.worx.device.client.data.DAO.DraftDAO
import id.worx.device.client.model.SubmitForm

@Database(entities = [SubmitForm::class], version = 1, exportSchema = true)
@TypeConverters(SubmitFormTypeConverter::class)
abstract class DraftDB : RoomDatabase() {

    abstract fun dao(): DraftDAO

    companion object {
        @Volatile
        private var instance: DraftDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                DraftDB::class.java,
                "draft.db"
            ).fallbackToDestructiveMigration()
                .build()
    }
}