package id.worx.mobile.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.worx.mobile.data.dao.FormDAO
import id.worx.mobile.model.EmptyForm


@Database(entities = [EmptyForm::class], version = 1, exportSchema = true)
@TypeConverters(SubmitFormTypeConverter::class)
abstract class FormDB : RoomDatabase() {

    abstract fun dao(): FormDAO

    companion object {
        @Volatile
        private var instance: FormDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                FormDB::class.java,
                "form.db"
            ).fallbackToDestructiveMigration().build()
    }
}