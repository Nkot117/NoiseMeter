package com.nkot117.noisemeter.data

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import jakarta.inject.Inject

class AudioRecordManager @Inject constructor() {
    // AudioRecord
    lateinit var audioRecord: AudioRecord
    private var bufferSize: Int = 0

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording(): AudioRecord {
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
        return audioRecord
    }

    fun stopRecording() {
        audioRecord.stop()
    }

    fun getBufferSize(): Int {
        return bufferSize
    }
}