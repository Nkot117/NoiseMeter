package com.nkot117.noisemeter.domain.usecase

import javax.inject.Inject

class CalculateAverageNoiseLevelUseCase @Inject constructor() {
    operator fun invoke(levels: ArrayList<Int>): Int {
        return levels.map {
            it
        }.average().toInt()
    }
}