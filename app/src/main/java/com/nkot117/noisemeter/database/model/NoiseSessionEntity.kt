package com.nkot117.noisemeter.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "noise_session")
data class NoiseSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "start_at") val startAt: Long,
    @ColumnInfo(name = "end_at") val endAt: Long,
    @ColumnInfo(name = "average_db") val averageDb: Int,
    @ColumnInfo(name = "max_db") val maxDb: Int,
    @ColumnInfo(name = "min_db") val minDb: Int
)