package ai.julie.feature.promptlab.di

import ai.julie.feature.promptlab.data.repository.EvaluationCriteriaRepositoryImpl
import ai.julie.feature.promptlab.data.repository.ProjectRepositoryImpl
import ai.julie.feature.promptlab.data.repository.PromptTemplateRepositoryImpl
import ai.julie.feature.promptlab.data.repository.TestSessionRepositoryImpl
import ai.julie.feature.promptlab.data.repository.WorkspaceRepositoryImpl
import ai.julie.feature.promptlab.model.provider.PromptLabModelProvider
import ai.julie.feature.promptlab.model.provider.PromptLabModelProviderImpl
import ai.julie.feature.promptlab.repository.EvaluationCriteriaRepository
import ai.julie.feature.promptlab.repository.ProjectRepository
import ai.julie.feature.promptlab.repository.PromptTemplateRepository
import ai.julie.feature.promptlab.repository.TestSessionRepository
import ai.julie.feature.promptlab.repository.WorkspaceRepository
import ai.julie.feature.promptlab.ui.project.ProjectListViewModel
import ai.julie.feature.promptlab.ui.prompttemplate.PromptTemplateViewModel
import ai.julie.feature.promptlab.ui.templatelist.PromptTemplateListViewModel
import ai.julie.feature.promptlab.ui.test.TestSessionViewModel
import ai.julie.feature.promptlab.ui.workspace.WorkspaceListViewModel
import ai.julie.feature.promptlab.usecase.CreateProjectUseCase
import ai.julie.feature.promptlab.usecase.CreatePromptTemplateUseCase
import ai.julie.feature.promptlab.usecase.CreateWorkspaceUseCase
import ai.julie.feature.promptlab.usecase.TestPromptUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private const val PROMPTLAB_DB_NAME = "julie-db" // Using same DB as main app

val promptLabModule = module {
    // Repositories
    single<WorkspaceRepository> {
        WorkspaceRepositoryImpl(dbName = PROMPTLAB_DB_NAME)
    }

    single<ProjectRepository> {
        ProjectRepositoryImpl(dbName = PROMPTLAB_DB_NAME)
    }

    single<PromptTemplateRepository> {
        PromptTemplateRepositoryImpl(dbName = PROMPTLAB_DB_NAME)
    }

    single<TestSessionRepository> {
        TestSessionRepositoryImpl(dbName = PROMPTLAB_DB_NAME)
    }

    single<EvaluationCriteriaRepository> {
        EvaluationCriteriaRepositoryImpl(dbName = PROMPTLAB_DB_NAME)
    }

    // Model Provider
    single<PromptLabModelProvider> {
        PromptLabModelProviderImpl()
    }

    // Use Cases
    single { CreateWorkspaceUseCase(get()) }
    single { CreateProjectUseCase(get()) }
    single { CreatePromptTemplateUseCase(get()) }
    single { TestPromptUseCase(get(), get(), get()) }

    // ViewModels
    viewModel { WorkspaceListViewModel(get(), get()) }
    viewModel { (workspaceId: String) ->
        ProjectListViewModel(workspaceId, get(), get())
    }
    viewModel { (projectId: String) ->
        PromptTemplateListViewModel(projectId, get())
    }
    viewModel { (projectId: String, templateId: String?) ->
        PromptTemplateViewModel(projectId, templateId, get(), get())
    }
    viewModel { (templateId: String) ->
        TestSessionViewModel(templateId)
    }
}