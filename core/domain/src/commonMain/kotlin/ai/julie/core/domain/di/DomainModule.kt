package ai.julie.core.domain.di

import ai.julie.core.data.di.dataModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import ai.julie.core.domain.CreateChatCompletionUseCase

val domainModule = module {
    includes(dataModule)

    factoryOf(::CreateChatCompletionUseCase)
}