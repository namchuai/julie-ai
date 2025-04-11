package ai.julie.di

import ai.julie.feature.chat.di.chatModule
import org.koin.dsl.module

val appModule = module {
    includes(chatModule)
}