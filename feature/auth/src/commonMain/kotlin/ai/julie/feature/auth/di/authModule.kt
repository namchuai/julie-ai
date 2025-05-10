package ai.julie.feature.auth.di

import ai.julie.core.domain.di.domainModule
import org.koin.dsl.module

val authModule = module {
    includes(domainModule)
}