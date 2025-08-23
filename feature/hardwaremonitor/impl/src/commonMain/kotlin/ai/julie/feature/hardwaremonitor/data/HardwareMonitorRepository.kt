package ai.julie.feature.hardwaremonitor.data

import ai.julie.feature.hardwaremonitor.domain.FlowOfCpuUsage
import ai.julie.feature.hardwaremonitor.domain.FlowOfHardwareStats
import ai.julie.feature.hardwaremonitor.domain.FlowOfMemoryUsage
import ai.julie.feature.hardwaremonitor.domain.HardwareStats
import kotlinx.coroutines.flow.Flow

expect class HardwareMonitorRepository() : FlowOfHardwareStats, FlowOfCpuUsage, FlowOfMemoryUsage {
    override fun flowOfHardwareStats(): Flow<HardwareStats>
    override fun flowOfCpuUsage(): Flow<Double>
    override fun flowOfMemoryUsage(): Flow<Double>
}