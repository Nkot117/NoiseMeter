package com.nkot117.noisemeter.data.datasource

import com.nkot117.noisemeter.database.dao.NoiseSessionDao
import com.nkot117.noisemeter.database.model.NoiseSessionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoiseSessionLocalDataSource @Inject constructor(
    private val dao: NoiseSessionDao
) {
    suspend fun insert(noiseSession: NoiseSessionEntity) {
        dao.insert(noiseSession)
    }

    fun getAll(): Flow<List<NoiseSessionEntity>> {
        return dao.getAll()
    }

    suspend fun delete(id: Long) {
        dao.delete(id)
    }

    suspend fun clear() {
        dao.clear()
    }
}