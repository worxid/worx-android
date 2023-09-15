package id.worx.mobile.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.worx.mobile.data.dao.SubmitFormDAO
import id.worx.mobile.model.SubmitForm

@Database(entities = [SubmitForm::class], version = 1, exportSchema = true)
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
            ).fallbackToDestructiveMigration().build()
    }
}