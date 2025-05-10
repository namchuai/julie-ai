package ai.julie.feature.pythonrunner.di

import ai.julie.feature.pythonrunner.PythonExecutor
import ai.julie.feature.pythonrunner.domain.ExecuteCode
import ai.julie.feature.pythonrunner.domain.ExecuteCodeAndGetString
import ai.julie.feature.pythonrunner.domain.GetInstalledPackages
import ai.julie.feature.pythonrunner.domain.InstallPackage
import ai.julie.feature.pythonrunner.ui.panel.PythonPanelViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val pythonRunnerModule = module {
    single<PythonExecutor> { PythonExecutor() }
    single<GetInstalledPackages> { get<PythonExecutor>() }
    single<ExecuteCode> { get<PythonExecutor>() }
    single<ExecuteCodeAndGetString> { get<PythonExecutor>() }
    single<InstallPackage> { get<PythonExecutor>() }

    viewModelOf(::PythonPanelViewModel)
}