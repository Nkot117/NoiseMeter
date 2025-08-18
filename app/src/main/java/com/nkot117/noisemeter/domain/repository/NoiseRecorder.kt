package com.nkot117.noisemeter.domain.repository

import kotlinx.coroutines.flow.Flow

interface NoiseRepository {
    fun readDbLevel(): Flow<Int>   // dBのFlow
}