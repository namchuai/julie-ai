package ai.julie.logging.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import ai.julie.logging.Logger

val loggingModule = module {
    factoryOf(::Logger)
}
