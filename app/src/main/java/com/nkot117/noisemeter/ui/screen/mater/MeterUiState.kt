package com.nkot117.noisemeter.ui.screen.mater

sealed class MeterUiState {
    object Initial : MeterUiState()
    data class Recording(val db: Int) : MeterUiState()
    data class Stopped(val sessionUiData: MeterSessionUiData) : MeterUiState()
    data class Error(val message: String) : MeterUiState()
}

data class MeterSessionUiData(
    val lastDb: Int,
    val averageDb: Int,
    val maxDb: Int,
    val minDb: Int,
)
