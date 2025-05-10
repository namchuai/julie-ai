package ai.julie.feature.hardwaremonitor.data

import ai.julie.feature.hardwaremonitor.domain.CpuStats
import ai.julie.feature.hardwaremonitor.domain.FlowOfCpuUsage
import ai.julie.feature.hardwaremonitor.domain.FlowOfHardwareStats
import ai.julie.feature.hardwaremonitor.domain.FlowOfMemoryUsage
import ai.julie.feature.hardwaremonitor.domain.GpuStats
import ai.julie.feature.hardwaremonitor.domain.HardwareStats
import ai.julie.feature.hardwaremonitor.domain.MemoryStats
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import oshi.hardware.GlobalMemory
import oshi.hardware.GraphicsCard

actual class HardwareMonitorRepository : FlowOfHardwareStats, FlowOfCpuUsage, FlowOfMemoryUsage {
    private val systemInfo = SystemInfo()
    private val hardware = systemInfo.hardware
    private var prevTicks = LongArray(CentralProcessor.TickType.entries.size)

    actual override fun flowOfHardwareStats(): Flow<HardwareStats> = callbackFlow {
        // Initialize the ticks for the first measurement
        prevTicks = hardware.processor.systemCpuLoadTicks
        delay(1000) // Wait for initial measurement

        while (isActive) {
            trySend(getCurrentHardwareStats())
            delay(1000) // Update every second
        }

        awaitClose {}
    }

    private fun getCurrentHardwareStats(): HardwareStats {
        val processor = hardware.processor
        val memory = hardware.memory
        val graphicsCards = hardware.graphicsCards

        return HardwareStats(
            cpu = getCpuStats(processor),
            memory = getMemoryStats(memory),
            gpus = getGpuStats(graphicsCards)
        )
    }

    private fun getCpuStats(processor: CentralProcessor): CpuStats {
        // Calculate CPU usage between ticks
        val cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100
        prevTicks = processor.systemCpuLoadTicks

        return CpuStats(
            model = processor.processorIdentifier.name,
            cores = processor.physicalProcessorCount,
            threads = processor.logicalProcessorCount,
            usage = cpuLoad
        )
    }

    private fun getMemoryStats(memory: GlobalMemory): MemoryStats {
        val total = memory.total
        val available = memory.available
        val used = total - available
        val usagePercentage = (used.toDouble() / total.toDouble()) * 100

        return MemoryStats(
            total = total,
            used = used,
            available = available,
            usagePercentage = usagePercentage
        )
    }

    private fun getGpuStats(graphicsCards: List<GraphicsCard>): List<GpuStats> {
        return graphicsCards.map { gpu ->
            GpuStats(
                name = gpu.name,
                memoryTotal = gpu.vRam,
                memoryUsed = null, // OSHI doesn't provide GPU memory usage easily
                usage = null, // OSHI doesn't provide GPU usage easily
                temperature = null
            )
        }
    }

    actual override fun flowOfCpuUsage(): Flow<Double> = callbackFlow {
        // Initialize the ticks for the first measurement
        var cpuPrevTicks = hardware.processor.systemCpuLoadTicks

        while (isActive) {
            val cpuLoad = hardware.processor.getSystemCpuLoadBetweenTicks(cpuPrevTicks) * 100
            cpuPrevTicks = hardware.processor.systemCpuLoadTicks
            trySend(cpuLoad)
            delay(1000) // Update every second
        }

        awaitClose { /* Cleanup if needed */ }
    }

    actual override fun flowOfMemoryUsage(): Flow<Double> = callbackFlow {
        while (isActive) {
            val memory = hardware.memory
            val total = memory.total
            val available = memory.available
            val used = total - available
            val usagePercentage = (used.toDouble() / total.toDouble()) * 100

            trySend(usagePercentage)
            delay(1000) // Update every second
        }

        awaitClose { /* Cleanup if needed */ }
    }
}