package com.nkot117.noisemeter.ui.noise

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoiseMeterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<NoiseUiState>(NoiseUiState.Initial)
    val uiState: StateFlow<NoiseUiState> = _uiState.asStateFlow()

    fun startRecording() {
        _uiState.value = NoiseUiState.Loading

        try {
            _uiState.value = NoiseUiState.Success(dbLevel = 0F)
        } catch (e: Exception) {
            _uiState.value = NoiseUiState.Error(message = "Error")
        }
    }

}
