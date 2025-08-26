package com.nkot117.noisemeter.data.datasource

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.sqrt

class AudioRecordDeviceDataSource @Inject constructor() {
    // AudioRecord
    lateinit var audioRecord: AudioRecord
    private var bufferSize: Int = 0

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun readDbLevel(): Flow<Int> = flow {
        // TODO 端末が対応しているサンプリングレートを検出する
        val samplingRate = 44100
        bufferSize = AudioRecord.getMinBufferSize(
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

        val buffer = ShortArray(bufferSize)
        try {
            while (currentCoroutineContext().isActive) {
                audioRecord.read(buffer, 0, buffer.size)
                val sum = buffer.sumOf { it.toDouble() * it.toDouble() }
                val amplitude = sqrt(sum / bufferSize).coerceAtLeast(1.0)
                val db = (20.0 * log10(amplitude)).toInt()
                emit(db)
                delay(100)
            }
        } catch (e: Exception) {
            Timber.d("Recording Error：%s", e.message)
            throw e
        } finally {
            audioRecord.stop()
        }

    }
}