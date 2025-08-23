package ai.julie.core.domain.di

import ai.julie.core.domain.prompt.JinjavaPromptTemplate
import ai.julie.core.domain.prompt.PromptTemplate
import ai.julie.core.domain.prompt.PromptTemplateRepository
import ai.julie.core.domain.prompt.PromptTemplateRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val desktopDomainModule = module {
    // Provide Jinjava implementation for desktop
    single<PromptTemplate> { JinjavaPromptTemplate() }

    // Provide repository implementation
    singleOf(::PromptTemplateRepositoryImpl) bind PromptTemplateRepository::class
}