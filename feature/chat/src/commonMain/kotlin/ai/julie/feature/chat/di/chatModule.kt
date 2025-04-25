package ai.julie.feature.chat.di

import ai.julie.core.domain.di.domainModule
import ai.julie.feature.chat.ChatViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val chatModule = module {
    includes(domainModule)
    viewModelOf(::ChatViewModel)
}