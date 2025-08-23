package ai.julie.feature.toolexecutor.di

import ai.julie.feature.toolexecutor.data.ToolCallHandlerImpl
import ai.julie.feature.toolexecutor.domain.ToolCallHandler
import org.koin.dsl.module

val toolExecutorModule = module {
    single<ToolCallHandler> { ToolCallHandlerImpl() }
}