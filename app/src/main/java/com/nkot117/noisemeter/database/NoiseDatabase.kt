package com.nkot117.noisemeter.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nkot117.noisemeter.database.dao.NoiseSessionDao
import com.nkot117.noisemeter.database.model.NoiseSessionEntity

@Database(
    entities = [NoiseSessionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class NoiseDatabase : RoomDatabase() {
    abstract fun noiseSessionDao(): NoiseSessionDao
}