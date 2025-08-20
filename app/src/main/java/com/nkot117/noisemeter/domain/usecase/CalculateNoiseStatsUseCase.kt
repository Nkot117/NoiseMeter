package com.nkot117.noisemeter.domain.usecase

import com.nkot117.noisemeter.domain.model.NoiseStats
import javax.inject.Inject

class CalculateNoiseStatsUseCase @Inject constructor() {
    operator fun invoke(samples: MutableList<Int>): NoiseStats {
        val minDb = samples.min()
        val maxDb = samples.max()
        val averageDb = samples.map {
            it
        }.average().toInt()

        return NoiseStats(
            minDb = minDb,
            maxDb = maxDb,
            averageDb = averageDb
        )
    }
}