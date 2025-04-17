package ai.julie.storage.di

import ai.julie.storage.Database
import ai.julie.storage.DatabaseDriverFactory
import ai.julie.storage.createDbDriverFactory
import org.koin.dsl.module

val storageModule = module {
    single<DatabaseDriverFactory> { createDbDriverFactory() }
    single { Database(get()) }
}