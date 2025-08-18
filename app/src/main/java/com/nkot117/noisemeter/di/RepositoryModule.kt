package com.nkot117.noisemeter.di

import com.nkot117.noisemeter.data.repository.NoiseRepositoryImpl
import com.nkot117.noisemeter.domain.repository.NoiseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNoiseRepository(
        impl: NoiseRepositoryImpl
    ): NoiseRepository
}