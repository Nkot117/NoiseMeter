package com.nkot117.noisemeter.ui.screen.noise

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkot117.noisemeter.domain.usecase.CalculateAverageNoiseLevelUseCase
import com.nkot117.noisemeter.domain.usecase.GetNoiseLevelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class NoiseMeterViewModel @Inject constructor(
    private val getNoiseLevelUseCase: GetNoiseLevelUseCase,
    private val averageNoiseLevelUseCase: CalculateAverageNoiseLevelUseCase
) : ViewModel() {
    // UiState
    private val _uiState = MutableStateFlow<NoiseUiState>(NoiseUiState.Initial)
    val uiState: StateFlow<NoiseUiState> = _uiState.asStateFlow()

    private var recordingJob: Job? = null
    private var correctDbList: ArrayList<Int> = arrayListOf()

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording() {
        Timber.d("Recording Start")
        _uiState.value = NoiseUiState.Recording(dbLevel = 0)

        try {
            recordingJob = viewModelScope.launch(Dispatchers.Default) {
                getNoiseLevelUseCase().collect { db ->
                    _uiState.value = NoiseUiState.Recording(dbLevel = db)
                    correctDbList.add(db)
                    Timber.d("Current:%s", db)
                }
            }
        } catch (e: Exception) {
            Timber.d("Recording Error：%s", e.message)
            _uiState.value = NoiseUiState.Error(message = "Error")
        }
    }

    fun stopRecording() {
        val currentDbLevel = (_uiState.value as? NoiseUiState.Recording)?.dbLevel ?: 0
        val averageDb = averageNoiseLevelUseCase(correctDbList)
        _uiState.value = NoiseUiState.Stopped(dbLevel = currentDbLevel, averageDb = averageDb)
        correctDbList = arrayListOf()
        try {
            recordingJob?.cancel()
            recordingJob = null
        } catch (e: Exception) {
            _uiState.value = NoiseUiState.Error(e.message.toString())
        }
    }
}
