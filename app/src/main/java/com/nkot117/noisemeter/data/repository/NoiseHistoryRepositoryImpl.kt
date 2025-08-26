package com.nkot117.noisemeter.data.repository

import com.nkot117.noisemeter.data.datasource.NoiseSessionLocalDataSource
import com.nkot117.noisemeter.database.model.NoiseSessionEntity
import com.nkot117.noisemeter.di.IoDispatcher
import com.nkot117.noisemeter.domain.model.NoiseSession
import com.nkot117.noisemeter.domain.repository.NoiseHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

class NoiseHistoryRepositoryImpl @Inject constructor(
    private val noiseSessionLocalDataSource: NoiseSessionLocalDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NoiseHistoryRepository {
    override suspend fun save(session: NoiseSession) {
        withContext(ioDispatcher) {
            noiseSessionLocalDataSource.insert(session.toEntity())
        }
    }

    override fun getAll(): Flow<List<NoiseSession>> {
        return noiseSessionLocalDataSource.getAll().flowOn(ioDispatcher)
            .map { entityList -> entityList.map { entity -> entity.toDomain() } }
    }

    override suspend fun delete(id: Long) {
        withContext(ioDispatcher) {
            noiseSessionLocalDataSource.delete(id)
        }
    }

    override suspend fun clear() {
        withContext(ioDispatcher) {
            noiseSessionLocalDataSource.clear()
        }
    }

    private fun NoiseSession.toEntity(): NoiseSessionEntity {
        return NoiseSessionEntity(
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