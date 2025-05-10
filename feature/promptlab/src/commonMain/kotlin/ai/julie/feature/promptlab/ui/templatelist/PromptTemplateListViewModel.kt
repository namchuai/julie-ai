package ai.julie.feature.promptlab.ui.templatelist

import ai.julie.feature.promptlab.model.PromptTemplate
import ai.julie.feature.promptlab.repository.PromptTemplateRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PromptTemplateListViewModel(
    private val projectId: String,
    private val promptTemplateRepository: PromptTemplateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromptTemplateListUiState())
    val uiState: StateFlow<PromptTemplateListUiState> = _uiState.asStateFlow()

    init {
        loadTemplates()
    }

    fun loadTemplates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                promptTemplateRepository.observeByProjectId(projectId)
                    .collect { templates ->
                        _uiState.update {
                            it.copy(
                                templates = templates.sortedByDescending { template -> template.updatedAt },
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load templates"
                    )
                }
            }
        }
    }

    fun refreshTemplates() {
        loadTemplates()
    }
}

data class PromptTemplateListUiState(
    val templates: List<PromptTemplate> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)