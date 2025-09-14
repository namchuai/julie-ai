package ai.julie.core.domain.di

import ai.julie.core.data.di.dataModule
import ai.julie.core.domain.DownloadWhisperModelUseCase
import ai.julie.core.domain.GetWhisperModelsUseCase
import ai.julie.core.domain.InitWhisperModelUseCase
import ai.julie.core.domain.ManageWhisperSystemUseCase
import ai.julie.core.domain.RecordAndTranscribeUseCase
import ai.julie.core.domain.RequestMicrophonePermissionUseCase
import ai.julie.core.domain.TranscribeAudioUseCase
import ai.julie.core.domain.model.download.DownloadModelViaUrlUseCase
import ai.julie.core.domain.session.FlowOfPromptSession
import ai.julie.core.domain.session.PromptSessionManager
import ai.julie.core.domain.session.PromptSessionManagerImpl
import ai.julie.core.network.di.networkModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    includes(dataModule)
    includes(networkModule)

    factoryOf(::DownloadModelViaUrlUseCase)
    factoryOf(::InitWhisperModelUseCase)
    factoryOf(::TranscribeAudioUseCase)
    factoryOf(::ManageWhisperSystemUseCase)
    factoryOf(::RequestMicrophonePermissionUseCase)
    factoryOf(::DownloadWhisperModelUseCase)
    factoryOf(::GetWhisperModelsUseCase)
    factoryOf(::RecordAndTranscribeUseCase)
    
    // Session management
    single {
        PromptSessionManagerImpl(
            processChatTemplate = get()
        )
    }
    single<PromptSessionManager> { get<PromptSessionManagerImpl>() }
    single<FlowOfPromptSession> { get<PromptSessionManagerImpl>() }
}