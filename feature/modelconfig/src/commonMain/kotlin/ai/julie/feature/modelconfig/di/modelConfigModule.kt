package ai.julie.feature.modelconfig.di

import ai.julie.feature.modelconfig.screen.modelconfig.ModelConfigViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val modelConfigModule = module {
    viewModelOf(::ModelConfigViewModel)
}