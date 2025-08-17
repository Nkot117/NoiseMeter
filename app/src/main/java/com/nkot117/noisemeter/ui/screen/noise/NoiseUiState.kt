package com.nkot117.noisemeter.ui.screen.noise

sealed class NoiseUiState {
    object Initial: NoiseUiState()
    data class Recording(val dbLevel: Int): NoiseUiState()
    data class Stopped(val dbLevel: Int, val averageDb: Int): NoiseUiState()
    data class Error(val message: String) : NoiseUiState()
}
