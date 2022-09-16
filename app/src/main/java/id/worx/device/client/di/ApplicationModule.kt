package id.worx.device.client.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.worx.device.client.data.api.WorxApi

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    fun provideAPIService(): WorxApi =
        WorxApi.create()
}