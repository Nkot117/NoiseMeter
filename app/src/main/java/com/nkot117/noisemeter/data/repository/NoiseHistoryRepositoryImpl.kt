package com.nkot117.noisemeter.data.repository

import com.nkot117.noisemeter.data.datasource.NoiseSessionLocalDataSource
import com.nkot117.noisemeter.database.model.NoiseSessionEntity
import com.nkot117.noisemeter.domain.model.NoiseSession
import com.nkot117.noisemeter.domain.repository.NoiseHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class NoiseHistoryRepositoryImpl @Inject constructor(
    private val noiseSessionLocalDataSource: NoiseSessionLocalDataSource
) : NoiseHistoryRepository {
    override suspend fun save(session: NoiseSession) {
        noiseSessionLocalDataSource.insert(session.toEntity())
    }

    override fun getAll(): Flow<List<NoiseSession>> {
        return noiseSessionLocalDataSource.getAll()
            .map { entityList -> entityList.map { entity -> entity.toDomain() } }
    }

    override suspend fun delete(id: Long) {
        noiseSessionLocalDataSource.delete(id)
    }

    override suspend fun clear() {
        noiseSessionLocalDataSource.clear()
    }

    private fun NoiseSession.toEntity(): NoiseSessionEntity {
        return NoiseSessionEntity(
            id = id,
            startAt = startAt.toEpochMilli(),
            endAt = endAt.toEpochMilli(),
            averageDb = averageDb,
            maxDb = maxDb,
            minDb = minDb
        )
    }

    private fun NoiseSessionEntity.toDomain(): NoiseSession {
        return NoiseSession(
            id = id,
            startAt = Instant.ofEpochMilli(startAt),
            endAt = Instant.ofEpochMilli(endAt),
            averageDb = averageDb,
            maxDb = maxDb,
            minDb = minDb
        )
    }
}