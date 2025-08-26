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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    /**
     * UiState
     */
    private val _uiState = MutableStateFlow<NoiseUiState>(NoiseUiState.Initial)
    val uiState: StateFlow<NoiseUiState> = _uiState.asStateFlow()

    /**
     * 収集Job
     */
    private var recordingJob: Job? = null

    /**
     * 収集中のDBリスト
     */
    private var samples = mutableListOf<Int>()

    /**
     * 収集開始時刻(UTC)
     */
    private var recordingStartAt: Instant? = null

    /**
     * DBの収集を開始する
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording() {
        Timber.d("Recording Start")
        recordingStartAt = Instant.now()
        _uiState.value = NoiseUiState.Recording(db = 0)

        // 収集開始
        recordingJob = viewModelScope.launch {
            getNoiseLevelUseCase().catch { e ->
                Timber.d("Recording Error：%s", e.message)
                _uiState.value = NoiseUiState.Error(message = "Error")
            }.collect { db ->
                Timber.d("Current:%s", db.db)
                _uiState.value = NoiseUiState.Recording(db = db.db)
                samples += db.db
            }
        }
    }

    /**
     * DBの収集を停止する
     */
    fun stopRecording() {
        Timber.d("Recording Stop")

        // 収集を終了
        recordingJob?.cancel()
        recordingJob = null

        // 収集結果の取得・保存
        val startAt = recordingStartAt!!
        val lastDb = (_uiState.value as? NoiseUiState.Recording)?.db ?: 0
        viewModelScope.launch {
            runCatching {
                val noiseStats = calculateNoiseStatsUseCase(samples.toList())

                saveNoiseSessionUseCase(
                    NoiseSession(
                        startAt = startAt,
                        endAt = Instant.now(),
                        averageDb = noiseStats.averageDb,
                        maxDb = noiseStats.maxDb,
                        minDb = noiseStats.minDb
                    )
                )
                noiseStats
            }.onFailure { e ->
                Timber.d("Save Error：%s", e.message)
                _uiState.value = NoiseUiState.Error(message = "Error")
            }.onSuccess { noiseStats ->
                _uiState.value = NoiseUiState.Stopped(
                    NoiseSessionUiData(
                        lastDb = lastDb,
                        averageDb = noiseStats.averageDb,
                        minDb = noiseStats.minDb,
                        maxDb = noiseStats.maxDb
                    )
                )
            }
        }

        // 初期化処理
        samples.clear()
        recordingStartAt = null
    }
}
