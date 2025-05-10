package ai.julie.feature.chat.di

import ai.julie.core.domain.di.domainModule
import ai.julie.feature.chat.domain.LocalInferenceUseCase
import ai.julie.feature.chat.ui.chat.ChatViewModel
import ai.julie.feature.chat.ui.chatinput.ChatInputViewModel
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

    viewModelOf(::ChatViewModel)
    viewModelOf(::ChatInputViewModel)
}