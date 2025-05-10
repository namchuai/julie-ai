package ai.julie.feature.hardwaremonitor.domain

import kotlinx.coroutines.flow.Flow

fun interface FlowOfMemoryUsage {
    fun flowOfMemoryUsage(): Flow<Double> // Memory usage percentage
}