package com.nkot117.noisemeter.di

import android.content.Context
import androidx.room.Room
import com.nkot117.noisemeter.database.NoiseSessionDatabase
import com.nkot117.noisemeter.database.dao.NoiseSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideNoiseDb(
        @ApplicationContext context: Context
    ): NoiseSessionDatabase =
        Room.databaseBuilder(context, NoiseSessionDatabase::class.java, "noise_session").build()

    @Singleton
    @Provides
    fun provideNoiseSessionDao(noiseSessionDatabase: NoiseSessionDatabase): NoiseSessionDao =
        noiseSessionDatabase.noiseSessionDao()

}