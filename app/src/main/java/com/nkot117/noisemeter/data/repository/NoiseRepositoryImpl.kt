package com.nkot117.noisemeter.data.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import com.nkot117.noisemeter.data.datasource.AudioRecordDeviceDataSource
import com.nkot117.noisemeter.domain.repository.NoiseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NoiseRepositoryImpl @Inject constructor(
    private val audioRecordDeviceDataSource: AudioRecordDeviceDataSource
) : NoiseRepository {
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun readDbLevel(): Flow<Int> = flow {
        audioRecordDeviceDataSource.readDbLevel().collect { db ->
            emit(db)
        }
    }
}