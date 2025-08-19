package com.nkot117.noisemeter.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nkot117.noisemeter.database.model.NoiseSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoiseSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg entity: NoiseSessionEntity)

    @Query("SELECT * FROM noise_session")
    fun getAll(): Flow<List<NoiseSessionEntity>>

    @Query("DELETE FROM noise_session WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM noise_session")
    suspend fun clear()
}