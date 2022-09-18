package id.worx.device.client.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.worx.device.client.data.DAO.DraftDAO
import id.worx.device.client.data.api.WorxApi
import id.worx.device.client.data.database.DraftDB
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    fun provideAPIService(): WorxApi =
        WorxApi.create()

    @Provides
    @Singleton
    fun provideDraftDao(draftDatabase: DraftDB): DraftDAO =
        draftDatabase.dao()

    @Provides
    @Singleton
    fun provideDraftDatabase(@ApplicationContext appContext: Context): DraftDB {
        return Room.databaseBuilder(
            appContext,
            DraftDB::class.java,
            "draft.db"
        ).fallbackToDestructiveMigration()
            .build()
    }
}