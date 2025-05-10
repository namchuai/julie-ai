package ai.julie.core.domain.di

import ai.julie.core.data.di.dataModule
import ai.julie.core.domain.CreateChatCompletionStreamUseCase
import ai.julie.core.domain.CreateChatCompletionUseCase
import ai.julie.core.domain.model.download.DownloadModelViaUrlUseCase
import ai.julie.core.domain.modelmanagement.GetDownloadedGgufFiles
import ai.julie.core.domain.thread.CreateThreadUseCase
import ai.julie.core.domain.thread.GetThreadFlowUseCase
import ai.julie.core.fs.di.fsModule
import ai.julie.core.network.di.networkModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    includes(dataModule)
    includes(fsModule)
    includes(networkModule)

    factoryOf(::CreateChatCompletionUseCase)
    factoryOf(::CreateChatCompletionStreamUseCase)
    factoryOf(::DownloadModelViaUrlUseCase)
    factoryOf(::CreateThreadUseCase)
    factoryOf(::GetThreadFlowUseCase)
    factoryOf(::GetDownloadedGgufFiles)
}