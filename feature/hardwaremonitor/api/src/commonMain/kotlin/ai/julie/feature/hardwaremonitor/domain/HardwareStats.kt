package ai.julie.feature.hardwaremonitor.domain

import kotlinx.datetime.Clock

data class CpuStats(
    val model: String,
    val cores: Int,
    val threads: Int,
    val usage: Double, // percentage
    val temperature: Double? = null // celsius
)

data class MemoryStats(
    val total: Long, // bytes
    val used: Long, // bytes
    val available: Long, // bytes
    val usagePercentage: Double
)

data class GpuStats(
    val name: String,
    val memoryTotal: Long? = null, // bytes
    val memoryUsed: Long? = null, // bytes
    val usage: Double? = null, // percentage
    val temperature: Double? = null // celsius
)

data class HardwareStats(
    val cpu: CpuStats,
    val memory: MemoryStats,
    val gpus: List<GpuStats> = emptyList(),
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)