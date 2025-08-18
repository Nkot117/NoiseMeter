package com.nkot117.noisemeter.data.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import com.nkot117.noisemeter.data.datasource.AudioRecordManager
import com.nkot117.noisemeter.domain.repository.NoiseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NoiseRepositoryImpl @Inject constructor(
    private val audioRecordManager: AudioRecordManager
) : NoiseRepository {
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override fun readDbLevel(): Flow<Int> = flow {
        audioRecordManager.readDbLevel().collect { db ->
            emit(db)
        }
    }
}