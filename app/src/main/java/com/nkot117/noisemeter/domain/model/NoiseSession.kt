package com.nkot117.noisemeter.domain.model

import java.time.Instant

data class NoiseSession(
    val id: Long? = 0L,
    val startAt: Instant,
    val endAt: Instant,
    val averageDb: Int,
    val maxDb: Int,
    val minDb: Int,
)
