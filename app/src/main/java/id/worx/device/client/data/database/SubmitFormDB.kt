package id.worx.device.client.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import id.worx.device.client.data.dao.SubmitFormDAO
import id.worx.device.client.model.SubmitForm

@Database(entities = [SubmitForm::class], version = 4, exportSchema = true)
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
            ).addMigrations(MIGRATION_3_4).build()

        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // no-op adding getSubmitForm in SubmitDAO
            }
        }
    }
}