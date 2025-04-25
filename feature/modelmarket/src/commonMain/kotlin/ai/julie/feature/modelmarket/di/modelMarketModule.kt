package ai.julie.feature.modelmarket.di

import ai.julie.core.domain.di.domainModule
import ai.julie.feature.modelmarket.screen.modelmarket.ModelMarketViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val modelMarketModule = module {
    includes(domainModule)
    viewModelOf(::ModelMarketViewModel)
}