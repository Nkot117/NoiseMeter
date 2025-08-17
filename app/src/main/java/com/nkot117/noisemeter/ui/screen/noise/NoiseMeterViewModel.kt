package com.nkot117.noisemeter.ui.screen.noise

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nkot117.noisemeter.data.AudioRecordManager
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
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.log10
import kotlin.math.sqrt

@HiltViewModel
class NoiseMeterViewModel @Inject constructor(
    private val audioRecordManager: AudioRecordManager
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

        val audioRecord = audioRecordManager.startRecording()
        val bufferSize = audioRecordManager.getBufferSize()

        try {
            recordingJob = viewModelScope.launch(Dispatchers.Default) {
                // 録音中は繰り返し処理を行う
                val buffer = ShortArray(bufferSize)
                while (isActive) {
                    audioRecord.read(buffer, 0, buffer.size)
                    val sum = buffer.sumOf { it.toDouble() * it.toDouble() }
                    val amplitude = sqrt(sum / bufferSize).coerceAtLeast(1.0)
                    val db = (20.0 * log10(amplitude)).toInt()

                    withContext(Dispatchers.Main) {
                        _uiState.value = NoiseUiState.Recording(dbLevel = db)
                    }

                    correctDbList.add(db)
                    delay(500)
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
        val correctCount = correctDbList.size
        val averageDb = correctDbList.sum() / correctCount
        _uiState.value = NoiseUiState.Stopped(dbLevel = currentDbLevel, averageDb = averageDb)

        correctDbList = arrayListOf()
        try {
            audioRecordManager.stopRecording()
            recordingJob?.cancel()
            recordingJob = null
        } catch (e: Exception) {
            _uiState.value = NoiseUiState.Error(e.message.toString())
        }
    }
}
