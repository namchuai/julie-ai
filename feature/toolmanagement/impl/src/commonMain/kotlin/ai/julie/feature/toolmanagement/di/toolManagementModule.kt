package ai.julie.feature.toolmanagement.di

import ai.julie.feature.toolmanagement.data.ToolRepository
import ai.julie.feature.toolmanagement.domain.CreateTool
import ai.julie.feature.toolmanagement.domain.DeleteTool
import ai.julie.feature.toolmanagement.domain.FlowOfTools
import ai.julie.feature.toolmanagement.domain.UpdateTool
import ai.julie.feature.toolmanagement.ui.ToolListingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private const val DB_NAME = "julie-db"

val toolManagementModule = module {
    
    // Tool repository implementation - create one instance and bind to all interfaces
    single { ToolRepository.create(dbName = DB_NAME) }
    single<CreateTool> { get<ToolRepository>() }
    single<FlowOfTools> { get<ToolRepository>() }
    single<UpdateTool> { get<ToolRepository>() }
    single<DeleteTool> { get<ToolRepository>() }
    
    // ViewModels
    viewModelOf(::ToolListingViewModel)
}