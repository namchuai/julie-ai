package ai.julie.di

import ai.julie.feature.chat.di.chatModule
import ai.julie.feature.modelmarket.di.modelMarketModule
import org.koin.dsl.module

val appModule = module {
    includes(chatModule)
    includes(modelMarketModule)
}