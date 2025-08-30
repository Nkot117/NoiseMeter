package com.nkot117.noisemeter.ui.screen.mater

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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class MeterViewModel @Inject constructor(
    private val getNoiseLevelUseCase: GetNoiseLevelUseCase,
    private val calculateNoiseStatsUseCase: CalculateNoiseStatsUseCase,
    private val saveNoiseSessionUseCase: SaveNoiseSessionUseCase
) : ViewModel() {
    /**
     * UiState
     */
    private val _uiState = MutableStateFlow<MeterUiState>(MeterUiState.Initial)
    val uiState: StateFlow<MeterUiState> = _uiState.asStateFlow()

    /**
     * Toast表示イベント
     */
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

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
        _uiState.value = MeterUiState.Recording(db = 0)

        // 収集開始
        recordingJob = viewModelScope.launch {
            _toastEvent.emit("測定を開始しました")

            getNoiseLevelUseCase().catch { e ->
                Timber.d("Recording Error：%s", e.message)
                _uiState.value = MeterUiState.Error(message = "Error")
            }.collect { db ->
                Timber.d("Current:%s", db.db)
                _uiState.value = MeterUiState.Recording(db = db.db)
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
        val lastDb = (_uiState.value as? MeterUiState.Recording)?.db ?: 0

        viewModelScope.launch {
            try {
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

                _uiState.value = MeterUiState.Stopped(
                    MeterSessionUiData(
                        lastDb = lastDb,
                        averageDb = noiseStats.averageDb,
                        minDb = noiseStats.minDb,
                        maxDb = noiseStats.maxDb
                    )
                )

                _toastEvent.emit("測定を終了しました")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                Timber.d("Save Error: %s", e.message)
                _uiState.value = MeterUiState.Error(message = "Error")
                _toastEvent.tryEmit("保存に失敗しました")
            }
        }

        // 初期化処理
        samples.clear()
        recordingStartAt = null
    }
}
