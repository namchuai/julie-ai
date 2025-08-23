package ai.julie.core.data.di

import ai.julie.core.data.llama.LlamaRepository
import ai.julie.core.data.permission.PermissionRepository
import ai.julie.core.data.permission.PermissionRepositoryImpl
import ai.julie.core.data.whisper.WhisperModelRepository
import ai.julie.core.data.whisper.WhisperModelRepositoryImpl
import ai.julie.core.data.whisper.WhisperRepository
import ai.julie.core.data.whisper.WhisperRepositoryImpl
import ai.julie.core.network.di.networkModule
import ai.julie.whisperbinding.createWhisperBinding
import org.koin.dsl.module

val dataModule = module {
    includes(networkModule)

    factory<LlamaRepository> { LlamaRepository() }

    single<WhisperRepository> {
        WhisperRepositoryImpl(createWhisperBinding())
    }
    single<WhisperModelRepository> {
        WhisperModelRepositoryImpl(get(), get())
    }
    single<PermissionRepository> {
        PermissionRepositoryImpl()
    }
}