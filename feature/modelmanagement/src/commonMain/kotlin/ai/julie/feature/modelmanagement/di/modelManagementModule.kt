package ai.julie.feature.modelmanagement.di

import ai.julie.core.domain.di.domainModule
import ai.julie.feature.modelmanagement.screen.localmodelmanagement.LocalModelManagementViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val modelManagementModule = module {
    includes(domainModule)
    viewModelOf(::LocalModelManagementViewModel)
}