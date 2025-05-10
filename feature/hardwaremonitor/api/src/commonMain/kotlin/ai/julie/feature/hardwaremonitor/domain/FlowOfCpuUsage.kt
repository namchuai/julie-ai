package ai.julie.feature.hardwaremonitor.domain

import kotlinx.coroutines.flow.Flow

fun interface FlowOfCpuUsage {
    fun flowOfCpuUsage(): Flow<Double> // CPU usage percentage
}