package ai.julie.core.domain.di

import ai.julie.core.data.di.dataModule
import org.koin.dsl.module

val domainModule = module {
    includes(dataModule)
}