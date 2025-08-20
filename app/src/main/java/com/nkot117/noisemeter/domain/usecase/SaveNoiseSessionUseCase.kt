package com.nkot117.noisemeter.domain.usecase

import com.nkot117.noisemeter.domain.model.NoiseSession
import com.nkot117.noisemeter.domain.repository.NoiseHistoryRepository
import javax.inject.Inject

class SaveNoiseSessionUseCase @Inject constructor(
    private val noiseSessionHistoryRepository: NoiseHistoryRepository
) {
    suspend operator fun invoke(session: NoiseSession) {
        noiseSessionHistoryRepository.save(session)
    }
}