package id.worx.device.client.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import id.worx.device.client.data.dao.FormDAO
import id.worx.device.client.model.EmptyForm


@Database(entities = [EmptyForm::class], version = 4, exportSchema = true)
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
            ).addMigrations(MIGRATION_3_4).build()

        private val MIGRATION_3_4: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // no-op adding getSortedAllForms in FormDAO
            }
        }
    }
}