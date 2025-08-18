package com.nkot117.noisemeter.domain.usecase

import com.nkot117.noisemeter.domain.model.DbLevel
import com.nkot117.noisemeter.domain.repository.NoiseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetNoiseLevelUseCase @Inject constructor(
    private val noiseRepository: NoiseRepository
) {
    operator fun invoke(): Flow<DbLevel> {
        return noiseRepository.readDbLevel().map {
            DbLevel(it)
        }
    }

}