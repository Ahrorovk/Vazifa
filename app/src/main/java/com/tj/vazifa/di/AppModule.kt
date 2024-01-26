package com.tj.vazifa.di

import android.content.Context
import com.tj.vazifa.data.local.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideSessionManager(
        @ApplicationContext context: Context
    ) = DataStoreManager(context)
}