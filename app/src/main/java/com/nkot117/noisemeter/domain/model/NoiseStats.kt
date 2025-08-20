package com.nkot117.noisemeter.domain.model

data class NoiseStats(
    val minDb: Int,
    val maxDb: Int,
    val averageDb: Int,
)
