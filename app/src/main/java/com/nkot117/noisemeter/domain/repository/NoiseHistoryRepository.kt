package com.nkot117.noisemeter.domain.repository

import com.nkot117.noisemeter.domain.model.NoiseSession
import kotlinx.coroutines.flow.Flow

interface NoiseHistoryRepository {
    suspend fun save(session: NoiseSession)
    fun getAll(): Flow<List<NoiseSession>>
    suspend fun delete(id: Long)
    suspend fun clear()
}