package ai.julie.core.domain.di

import ai.julie.core.data.di.dataModule
import ai.julie.core.domain.CreateChatCompletionUseCase
import ai.julie.core.domain.model.download.DownloadModelViaUrlUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    includes(dataModule)

    factoryOf(::CreateChatCompletionUseCase)
    factoryOf(::DownloadModelViaUrlUseCase)
}