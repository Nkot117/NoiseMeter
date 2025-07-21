package com.nkot117.noisemeter.ui.noise

sealed class NoiseUiState {
    object Initial: NoiseUiState()
    object Loading : NoiseUiState()
    data class Success(val dbLevel: Float) : NoiseUiState()
    data class Error(val message: String) : NoiseUiState()
}
