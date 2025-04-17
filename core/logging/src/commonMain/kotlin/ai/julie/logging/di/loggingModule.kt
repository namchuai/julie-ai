package ai.julie.logging.di

import ai.julie.logging.Logger
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val loggingModule = module {
    factoryOf(::Logger)
}
