package com.nkot117.noisemeter.domain.usecase

import com.nkot117.noisemeter.di.DefaultDispatcher
import com.nkot117.noisemeter.domain.model.NoiseStats
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

class CalculateNoiseStatsUseCase @Inject constructor(
    @param:DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(samples: List<Int>): NoiseStats {
        // 空リストが入ってきた場合、例外をスロー
        require(samples.isNotEmpty()) { "samples is empty." }

        return withContext(defaultDispatcher) {
            val minDb = samples.minOrNull() ?: 0
            val maxDb = samples.maxOrNull() ?: 0
            val averageDb = samples.average().roundToInt()

            NoiseStats(
                minDb = minDb,
                maxDb = maxDb,
                averageDb = averageDb
            )
        }
    }
}