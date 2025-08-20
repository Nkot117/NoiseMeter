package com.nkot117.noisemeter.ui.screen.noise

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkot117.noisemeter.domain.model.NoiseSession
import com.nkot117.noisemeter.domain.usecase.CalculateNoiseStatsUseCase
import com.nkot117.noisemeter.domain.usecase.GetNoiseLevelUseCase
import com.nkot117.noisemeter.domain.usecase.SaveNoiseSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class NoiseMeterViewModel @Inject constructor(
    private val getNoiseLevelUseCase: GetNoiseLevelUseCase,
    private val calculateNoiseStatsUseCase: CalculateNoiseStatsUseCase,
    private val saveNoiseSessionUseCase: SaveNoiseSessionUseCase
) : ViewModel() {
    // UiState
    private val _uiState = MutableStateFlow<NoiseUiState>(NoiseUiState.Initial)
    val uiState: StateFlow<NoiseUiState> = _uiState.asStateFlow()

    private var recordingJob: Job? = null
    private var correctDbList: ArrayList<Int> = arrayListOf()

    private var startAt: Instant? = null
    private var endAt: Instant? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording() {
        Timber.d("Recording Start")
        _uiState.value = NoiseUiState.Recording(dbLevel = 0)

        startAt = Instant.now()
        try {
            recordingJob = viewModelScope.launch(Dispatchers.Default) {
                getNoiseLevelUseCase().collect { dbLevel ->
                    _uiState.value = NoiseUiState.Recording(dbLevel = dbLevel.db)
                    correctDbList.add(dbLevel.db)
                    Timber.d("Current:%s", dbLevel.db)
                }
            }
        } catch (e: Exception) {
            Timber.d("Recording Error：%s", e.message)
            _uiState.value = NoiseUiState.Error(message = "Error")
        }
    }

    fun stopRecording() {
        val currentDb = (_uiState.value as? NoiseUiState.Recording)?.dbLevel ?: 0
        val noiseStats = calculateNoiseStatsUseCase(correctDbList)
        _uiState.value = NoiseUiState.Stopped(
            NoiseSessionUiData(
                currentDb = currentDb,
                averageDb = noiseStats.averageDb,
                minDb = noiseStats.minDb,
                maxDb = noiseStats.maxDb
            )
        )

        endAt = Instant.now()

        viewModelScope.launch(Dispatchers.Default) {
            saveNoiseSessionUseCase(
                NoiseSession(
                    startAt = startAt!!,
                    endAt = endAt!!,
                    averageDb = noiseStats.averageDb,
                    maxDb = noiseStats.maxDb,
                    minDb = noiseStats.minDb
                )
            )
        }

        correctDbList = arrayListOf()
        try {
            recordingJob?.cancel()
            recordingJob = null
        } catch (e: Exception) {
            _uiState.value = NoiseUiState.Error(e.message.toString())
        }
    }
}
