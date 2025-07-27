package com.nkot117.noisemeter.ui.noise

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoiseMeterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<NoiseUiState>(NoiseUiState.Initial)
    val uiState: StateFlow<NoiseUiState> = _uiState.asStateFlow()

    fun startRecording() {
        _uiState.value = NoiseUiState.Recording
        try {
            // TODO: AudioRecordを使用した録音機能
        } catch (e: Exception) {
            _uiState.value = NoiseUiState.Error(message = "Error")
        }
    }

    fun stopRecording() {
        _uiState.value = NoiseUiState.Initial
        try {
            // TODO: AudioRecordのリリースタスク
        } catch (e: Exception) {
            _uiState.value = NoiseUiState.Error(message = "Error")
        }
    }
}
