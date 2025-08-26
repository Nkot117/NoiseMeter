package com.nkot117.noisemeter.data.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import com.nkot117.noisemeter.data.datasource.AudioRecordDeviceDataSource
import com.nkot117.noisemeter.di.IoDispatcher
import com.nkot117.noisemeter.domain.repository.NoiseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NoiseRepositoryImpl @Inject constructor(
    private val audioRecordDeviceDataSource: AudioRecordDeviceDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NoiseRepository {
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun readDbLevel(): Flow<Int> =
        audioRecordDeviceDataSource.readDbLevel().flowOn(ioDispatcher)
}