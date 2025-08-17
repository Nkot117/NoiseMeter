package com.nkot117.noisemeter.ui.screen.noise

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.log10
import kotlin.math.sqrt

@HiltViewModel
class NoiseMeterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<NoiseUiState>(NoiseUiState.Initial)
    val uiState: StateFlow<NoiseUiState> = _uiState.asStateFlow()
    lateinit var audioRecord: AudioRecord
    private var recordingJob: Job? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording() {
        Timber.d("Recording Start")
        _uiState.value = NoiseUiState.Recording(dbLevel = 0)

        // TODO 端末が対応しているサンプリングレートを検出する
        val samplingRate = 44100
        val bufferSize = AudioRecord.getMinBufferSize(
            samplingRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            samplingRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        audioRecord.startRecording()

        try {
            recordingJob = viewModelScope.launch(Dispatchers.Default) {
                // 録音中は繰り返し処理を行う
                val buffer = ShortArray(bufferSize)
                while (isActive) {
                    audioRecord.read(buffer, 0, buffer.size)
                    val sum = buffer.sumOf { it.toDouble() * it.toDouble() }
                    val amplitude = sqrt(sum / bufferSize).coerceAtLeast(1.0)
                    val db = (20.0 * log10(amplitude)).toInt()
                    _uiState.value = NoiseUiState.Recording(dbLevel = db)
                    delay(100)

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
        _uiState.value = NoiseUiState.Stopped(currentDbLevel)
        try {
            audioRecord.stop()
            recordingJob?.cancel()
            recordingJob = null
        } catch (e: Exception) {
            _uiState.value = NoiseUiState.Error(e.message.toString())
        }
    }
}
