package ai.julie.feature.chat.di

import ai.julie.core.domain.di.domainModule
import ai.julie.feature.chat.domain.LocalInferenceUseCase
import ai.julie.feature.chat.domain.SimpleInferenceUseCase
import ai.julie.feature.chat.ui.chat.ChatViewModel
import ai.julie.feature.chat.ui.chatinput.ChatInputViewModel
import ai.julie.feature.toolexecutor.data.BraveSearchService
import ai.julie.feature.message.di.messageModule
import ai.julie.feature.modelmanagement.di.modelManagementModule
import ai.julie.feature.thread.di.threadModule
import ai.julie.feature.toolexecutor.di.toolExecutorModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatModule = module {
    includes(domainModule)
    includes(threadModule)
    includes(messageModule)
    includes(modelManagementModule)
    includes(toolExecutorModule)

    single { BraveSearchService() }

    factory {
        LocalInferenceUseCase(
            flowOfMessages = get(),
            promptSessionManager = get(),
            createMessage = get(),
            updateMessage = get(),
            flowOfModelMetadata = get(),
            flowOfLocalModels = get(),
            modelExecutionRepository = get(),
            toolCallHandler = get()
        )
    }

    factory {
        SimpleInferenceUseCase(
            flowOfMessages = get(),
            createMessage = get(),
            updateMessage = get(),
            flowOfModelMetadata = get(),
            flowOfLocalModels = get(),
            modelExecutionRepository = get(),
            braveSearchService = get()
        )
    }

    viewModelOf(::ChatViewModel)
    
    factory {
        ChatInputViewModel(
            flowOfActiveThread = get(),
            flowOfStartingModels = get(),
            flowOfRunningModels = get(),
            flowOfSamplingPresets = get(),
            flowOfPromptSession = get(),
            createMessage = get(),
            localInferenceUseCase = get(),
            simpleInferenceUseCase = get(),
            validateModel = get(),
            eventBus = get(),
        )
    }
}