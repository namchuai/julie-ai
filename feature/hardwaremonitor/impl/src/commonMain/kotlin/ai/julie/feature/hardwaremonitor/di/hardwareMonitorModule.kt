package ai.julie.feature.hardwaremonitor.di

import ai.julie.feature.hardwaremonitor.data.HardwareMonitorRepository
import ai.julie.feature.hardwaremonitor.domain.FlowOfCpuUsage
import ai.julie.feature.hardwaremonitor.domain.FlowOfHardwareStats
import ai.julie.feature.hardwaremonitor.domain.FlowOfMemoryUsage
import ai.julie.feature.hardwaremonitor.ui.cpustatusbar.CpuStatusBarViewModel
import ai.julie.feature.hardwaremonitor.ui.memorystatusbar.MemoryStatusBarViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val hardwareMonitorModule = module {
    single { HardwareMonitorRepository() }
    single<FlowOfHardwareStats> { get<HardwareMonitorRepository>() }
    single<FlowOfCpuUsage> { get<HardwareMonitorRepository>() }
    single<FlowOfMemoryUsage> { get<HardwareMonitorRepository>() }

    viewModelOf(::CpuStatusBarViewModel)
    viewModelOf(::MemoryStatusBarViewModel)
}