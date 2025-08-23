package ai.julie.feature.modelmanagement.di

import ai.julie.core.domain.di.domainModule
import ai.julie.feature.modelconfig.di.modelConfigModule
import ai.julie.feature.modelmanagement.data.LocalModelRepository
import ai.julie.feature.modelmanagement.data.ModelExecutionRepository
import ai.julie.feature.modelmanagement.domain.AddLocalModelFromFileUseCase
import ai.julie.feature.modelmanagement.domain.DeleteLocalModel
import ai.julie.feature.modelmanagement.domain.DeleteLocalModelUseCase
import ai.julie.feature.modelmanagement.domain.FlowOfLocalModels
import ai.julie.feature.modelmanagement.domain.FlowOfRunningModels
import ai.julie.feature.modelmanagement.domain.FlowOfStartingModels
import ai.julie.feature.modelmanagement.domain.ReloadModel
import ai.julie.feature.modelmanagement.domain.StartModel
import ai.julie.feature.modelmanagement.domain.StopAllModels
import ai.julie.feature.modelmanagement.domain.StopModel
import ai.julie.feature.modelmanagement.domain.StoreLocalModel
import ai.julie.feature.modelmanagement.navigation.ModelManagementNavigation
import ai.julie.feature.modelmanagement.navigation.ModelManagementNavigationImpl
import ai.julie.feature.modelmanagement.ui.localmodelmanagement.LocalModelManagementViewModel
import ai.julie.feature.modelmanagement.ui.modelconfig.ModelConfigViewModel
import ai.julie.feature.modelmanagement.ui.modelinfo.ModelInfoViewModel
import ai.julie.feature.modelmanagement.ui.modelmetadata.ModelMetadataViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private const val DB_NAME = "julie-db"

val modelManagementModule = module {
    includes(domainModule)
    includes(modelConfigModule)

    // Feature Local Model Repository - create one instance and bind to all interfaces
    single<LocalModelRepository> {
        LocalModelRepository.create(dbName = DB_NAME)
    }
    single<StoreLocalModel> { get<LocalModelRepository>() }
    single<DeleteLocalModel> { get<LocalModelRepository>() }
    single<FlowOfLocalModels> { get<LocalModelRepository>() }

    // Use Cases
    singleOf(::AddLocalModelFromFileUseCase)
    singleOf(::DeleteLocalModelUseCase)

    // ViewModels
    viewModelOf(::LocalModelManagementViewModel)
    viewModelOf(::ModelConfigViewModel)
    viewModelOf(::ModelInfoViewModel)
    viewModelOf(::ModelMetadataViewModel)

    // Model execution bindings - create one instance and bind to all interfaces
    single<ModelExecutionRepository> { ModelExecutionRepository({ get() }, get(), get()) }
    single<FlowOfRunningModels> { get<ModelExecutionRepository>() }
    single<FlowOfStartingModels> { get<ModelExecutionRepository>() }
    single<StartModel> { get<ModelExecutionRepository>() }
    single<StopModel> { get<ModelExecutionRepository>() }
    single<StopAllModels> { get<ModelExecutionRepository>() }
    single<ReloadModel> { get<ModelExecutionRepository>() }

    // Navigation binding
    singleOf(::ModelManagementNavigationImpl) bind ModelManagementNavigation::class
}