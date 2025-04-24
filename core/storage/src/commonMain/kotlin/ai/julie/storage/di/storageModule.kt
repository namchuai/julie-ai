package ai.julie.storage.di

import ai.julie.storage.Database
import ai.julie.storage.DatabaseDriverFactory
import ai.julie.storage.JulieDatabase
import ai.julie.storage.MessageOperations
import ai.julie.storage.ThreadOperations
import ai.julie.storage.createDbDriverFactory
import ai.julie.storage.kotbase.JulieDatabaseImpl
import ai.julie.storage.kotbase.MessageOperationsImpl
import ai.julie.storage.kotbase.ThreadOperationsImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DB_NAME_KEY = "db_name"
private const val DB_NAME = "julie-db"

val storageModule = module {
    single<DatabaseDriverFactory> { createDbDriverFactory() }
    single { Database(get()) }

    single(named(DB_NAME_KEY)) { DB_NAME }
    single<ThreadOperations> { ThreadOperationsImpl.create(dbName = get(named(DB_NAME_KEY))) }
    single<MessageOperations> { MessageOperationsImpl(dbName = get(named(DB_NAME_KEY))) }

    single<JulieDatabase> {
        JulieDatabaseImpl(
            threadOperations = get(),
            messageOperations = get(),
        )
    }
}