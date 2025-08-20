package com.nkot117.noisemeter.ui.screen.noise

sealed class NoiseUiState {
    object Initial : NoiseUiState()
    data class Recording(val dbLevel: Int) : NoiseUiState()
    data class Stopped(val sessionUiData: NoiseSessionUiData) : NoiseUiState()
    data class Error(val message: String) : NoiseUiState()
}

data class NoiseSessionUiData(
    val currentDb: Int,
    val averageDb: Int,
    val maxDb: Int,
    val minDb: Int,
)
