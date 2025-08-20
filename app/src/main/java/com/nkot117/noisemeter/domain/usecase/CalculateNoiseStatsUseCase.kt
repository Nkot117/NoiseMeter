package com.nkot117.noisemeter.domain.usecase

import com.nkot117.noisemeter.domain.model.NoiseStats
import javax.inject.Inject

class CalculateNoiseStatsUseCase @Inject constructor() {
    operator fun invoke(levels: ArrayList<Int>): NoiseStats {
        val minDb = levels.min()
        val maxDb = levels.max()
        val averageDb = levels.map {
            it
        }.average().toInt()
        
        return NoiseStats(
            minDb = minDb,
            maxDb = maxDb,
            averageDb = averageDb
        )
    }
}