package ai.julie.feature.hardwaremonitor.domain

import kotlinx.coroutines.flow.Flow

fun interface FlowOfHardwareStats {
    fun flowOfHardwareStats(): Flow<HardwareStats>
}