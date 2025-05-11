package ai.julie.core.data.di

import ai.julie.core.data.llama.LlamaRepository
import ai.julie.core.data.llama.LlamaRepositoryImpl
import ai.julie.core.data.thread.ThreadRepository
import ai.julie.core.data.thread.ThreadRepositoryImpl
import ai.julie.core.network.di.networkModule
import ai.julie.storage.di.storageModule
import org.koin.dsl.module

val dataModule = module {
    includes(networkModule)
    includes(storageModule)

    single<ThreadRepository> {
        ThreadRepositoryImpl(get())
    }
    single<LlamaRepository> {
        LlamaRepositoryImpl()
    }
}