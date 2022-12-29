package id.worx.device.client.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.worx.device.client.data.dao.SubmitFormDAO
import id.worx.device.client.model.SubmitForm

@Database(entities = [SubmitForm::class], version = 3, exportSchema = true)
@TypeConverters(SubmitFormTypeConverter::class)
abstract class SubmitFormDB : RoomDatabase() {

    abstract fun dao(): SubmitFormDAO

    companion object {
        @Volatile
        private var instance: SubmitFormDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                SubmitFormDB::class.java,
                "draft.db"
            ).fallbackToDestructiveMigration()
                .build()
    }
}