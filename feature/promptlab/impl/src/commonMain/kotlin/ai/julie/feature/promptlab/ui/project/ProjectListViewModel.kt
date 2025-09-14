package ai.julie.feature.promptlab.ui.project

import ai.julie.feature.promptlab.model.Project
import ai.julie.feature.promptlab.repository.ProjectRepository
import ai.julie.feature.promptlab.usecase.CreateProjectUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProjectListViewModel(
    private val workspaceId: String,
    private val projectRepository: ProjectRepository,
    private val createProjectUseCase: CreateProjectUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectListUiState())
    val uiState: StateFlow<ProjectListUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            projectRepository.observeByWorkspaceId(workspaceId)
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .collect { projects ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            projects = projects,
                            error = null
                        )
                    }
                }
        }
    }

    fun createProject(name: String, description: String?, tags: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true) }

            createProjectUseCase(workspaceId, name, description, tags)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            showCreateDialog = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            error = error.message
                        )
                    }
                }
        }
    }

    fun showCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun hideCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }
}

data class ProjectListUiState(
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val projects: List<Project> = emptyList(),
    val showCreateDialog: Boolean = false,
    val error: String? = null
)