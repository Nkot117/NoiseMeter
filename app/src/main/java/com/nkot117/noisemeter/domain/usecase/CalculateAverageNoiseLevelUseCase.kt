package com.nkot117.noisemeter.domain.usecase

import com.nkot117.noisemeter.domain.model.DbLevel
import javax.inject.Inject

class CalculateAverageNoiseLevelUseCase @Inject constructor() {
    operator fun invoke(levels: ArrayList<Int>): DbLevel {
        return DbLevel(db = levels.map {
            it
        }.average().toInt())
    }
}