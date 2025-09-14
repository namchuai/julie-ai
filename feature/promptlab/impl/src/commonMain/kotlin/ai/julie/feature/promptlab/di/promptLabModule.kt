package ai.julie.feature.promptlab.di

import ai.julie.feature.promptlab.data.PromptTemplateRepository
import ai.julie.feature.promptlab.data.repository.EvaluationCriteriaRepositoryImpl
import ai.julie.feature.promptlab.data.repository.ProjectRepositoryImpl
import ai.julie.feature.promptlab.data.repository.TestSessionRepositoryImpl
import ai.julie.feature.promptlab.domain.CreatePromptTemplate
import ai.julie.feature.promptlab.domain.DeletePromptTemplate
import ai.julie.feature.promptlab.domain.FlowOfPromptTemplates
import ai.julie.feature.promptlab.domain.UpdatePromptTemplate
import ai.julie.feature.promptlab.model.provider.PromptLabModelProvider
import ai.julie.feature.promptlab.model.provider.PromptLabModelProviderImpl
import ai.julie.feature.promptlab.repository.EvaluationCriteriaRepository
import ai.julie.feature.promptlab.repository.ProjectRepository
import ai.julie.feature.promptlab.repository.TestSessionRepository
import ai.julie.feature.promptlab.repository.WorkspaceRepository
import ai.julie.feature.promptlab.repository.WorkspaceRepositoryImpl
import ai.julie.feature.promptlab.ui.project.ProjectListViewModel
import ai.julie.feature.promptlab.ui.prompttemplate.PromptTemplateViewModel
import ai.julie.feature.promptlab.ui.templatelist.PromptTemplateListViewModel
import ai.julie.feature.promptlab.ui.workspace.WorkspaceListViewModel
import ai.julie.feature.promptlab.usecase.CreateProjectUseCase
import ai.julie.feature.promptlab.usecase.CreateWorkspaceUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private const val DB_NAME = "julie-db"

val promptLabModule = module {
    // Repositories
    single<WorkspaceRepository> {
        WorkspaceRepositoryImpl(dbName = DB_NAME)
    }

    single<ProjectRepository> {
        ProjectRepositoryImpl(dbName = DB_NAME)
    }

    single<TestSessionRepository> {
        TestSessionRepositoryImpl(dbName = DB_NAME)
    }

    single<EvaluationCriteriaRepository> {
        EvaluationCriteriaRepositoryImpl(dbName = DB_NAME)
    }

    // New PromptTemplate Repository
    single<PromptTemplateRepository> {
        PromptTemplateRepository.create(dbName = DB_NAME)
    }
    single<FlowOfPromptTemplates> { get<PromptTemplateRepository>() }
    single<CreatePromptTemplate> { get<PromptTemplateRepository>() }
    single<UpdatePromptTemplate> { get<PromptTemplateRepository>() }
    single<DeletePromptTemplate> { get<PromptTemplateRepository>() }

    // Model Provider
    single<PromptLabModelProvider> {
        PromptLabModelProviderImpl()
    }

    // Use Cases
    single { CreateWorkspaceUseCase(get()) }
    single { CreateProjectUseCase(get()) }

    // ViewModels
    viewModel { WorkspaceListViewModel(get(), get()) }
    viewModel { (workspaceId: String) ->
        ProjectListViewModel(workspaceId, get(), get())
    }
    viewModel { PromptTemplateListViewModel(get()) }
    viewModel { (projectId: String, templateId: String?) ->
        PromptTemplateViewModel(
            projectId,
            templateId
        )
    }
}