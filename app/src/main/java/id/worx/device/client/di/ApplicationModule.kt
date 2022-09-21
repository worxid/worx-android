package id.worx.device.client.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.worx.device.client.WorxApplication
import id.worx.device.client.data.api.WorxApi
import id.worx.device.client.data.dao.FormDAO
import id.worx.device.client.data.dao.SubmitFormDAO
import id.worx.device.client.data.database.FormDB
import id.worx.device.client.data.database.SubmitFormDB
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    fun provideApplication(@ApplicationContext context: Context): WorxApplication =
        context as WorxApplication

    @Provides
    fun provideAPIService(): WorxApi =
        WorxApi.create()

    @Provides
    @Singleton
    fun provideDraftDao(database: SubmitFormDB): SubmitFormDAO =
        database.dao()

    @Provides
    @Singleton
    fun provideDraftDatabase(@ApplicationContext appContext: Context): SubmitFormDB {
        return Room.databaseBuilder(
            appContext,
            SubmitFormDB::class.java,
            "submit_form.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideFormDao(formDatabase: FormDB): FormDAO =
        formDatabase.dao()

    @Provides
    @Singleton
    fun provideFormDatabase(@ApplicationContext appContext: Context): FormDB {
        return Room.databaseBuilder(
            appContext,
            FormDB::class.java,
            "form.db"
        ).fallbackToDestructiveMigration()
            .build()
    }
}