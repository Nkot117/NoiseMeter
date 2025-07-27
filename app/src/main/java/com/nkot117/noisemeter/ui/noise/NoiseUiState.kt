package com.nkot117.noisemeter.ui.noise

sealed class NoiseUiState {
    object Initial: NoiseUiState()
    object isRecording: NoiseUiState()
    data class Error(val message: String) : NoiseUiState()
}
