package ai.julie.feature.promptlab.ui.workspace

import ai.julie.feature.promptlab.model.Workspace
import ai.julie.feature.promptlab.model.WorkspaceType
import ai.julie.feature.promptlab.repository.WorkspaceRepository
import ai.julie.feature.promptlab.usecase.CreateWorkspaceUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WorkspaceListViewModel(
    private val workspaceRepository: WorkspaceRepository,
    private val createWorkspaceUseCase: CreateWorkspaceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkspaceListUiState())
    val uiState: StateFlow<WorkspaceListUiState> = _uiState.asStateFlow()

    init {
        loadWorkspaces()
    }

    private fun loadWorkspaces() {
        viewModelScope.launch {
            workspaceRepository.observeAll()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                }
                .collect { workspaces ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            workspaces = workspaces,
                            error = null
                        )
                    }
                }
        }
    }

    fun createWorkspace(name: String, type: WorkspaceType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true) }

            createWorkspaceUseCase(name, type)
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

data class WorkspaceListUiState(
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val workspaces: List<Workspace> = emptyList(),
    val showCreateDialog: Boolean = false,
    val error: String? = null
)