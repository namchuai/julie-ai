package ai.julie.feature.promptlab.ui.prompttemplate

import ai.julie.core.common.AsyncState
import ai.julie.core.common.createAsyncLoading
import ai.julie.core.common.doNotReportLoadTime
import ai.julie.core.common.doNotSaveState
import ai.julie.core.common.viewModelState
import androidx.lifecycle.ViewModel

class PromptTemplateViewModel(
    private val projectId: String,
    private val templateId: String? = null,
) : ViewModel() {

    val state = viewModelState<AsyncState<PromptTemplateUiState>>(
        savedStateBehaviour = doNotSaveState(),
        initialState = createAsyncLoading(),
        loadTimeReporter = doNotReportLoadTime(),
    )

    fun updateName(name: String) {
//        _uiState.update { it.copy(name = name) }
    }

    fun updateContent(content: String) {
//        _uiState.update { it.copy(content = content) }
//        extractVariablesFromContent(content)
    }

//    private fun extractVariablesFromContent(content: String) {
//        val variablePattern = """\{\{(\w+)\}\}""".toRegex()
//        val foundVariables = variablePattern.findAll(content)
//            .map { it.groupValues[1] }
//            .toSet()
//
//        val currentVariables = _uiState.value.variables.toMutableMap()
//
//        // Add new variables
//        foundVariables.forEach { varName ->
//            if (!currentVariables.containsKey(varName)) {
//                currentVariables[varName] = VariableConfig(
//                    type = VariableType.STRING,
//                    description = null,
//                    defaultValue = null
//                )
//            }
//        }
//
//        // Remove variables not in content
//        val toRemove = currentVariables.keys - foundVariables
//        toRemove.forEach { currentVariables.remove(it) }
//
//        _uiState.update {
//            it.copy(
//                variables = currentVariables,
//                previewValues = foundVariables.associateWith { varName ->
//                    currentVariables[varName]?.defaultValue ?: ""
//                }
//            )
//        }
//    }

//    fun updateVariable(name: String, config: VariableConfig) {
//        _uiState.update { state ->
//            state.copy(
//                variables = state.variables + (name to config),
//                previewValues = state.previewValues + (name to (config.defaultValue ?: ""))
//            )
//        }
//    }

    fun removeVariable(name: String) {
//        _uiState.update { state ->
//            state.copy(
//                variables = state.variables - name,
//                previewValues = state.previewValues - name
//            )
//        }
    }

    fun addVariable() {
//        _uiState.update { it.copy(showAddVariableDialog = true) }
    }

    fun hideAddVariableDialog() {
//        _uiState.update { it.copy(showAddVariableDialog = false) }
    }

//    fun addVariableWithConfig(name: String, config: VariableConfig) {
//        updateVariable(name, config)
//        hideAddVariableDialog()
//    }

//    fun saveTemplate() {
//        val currentState = _uiState.value
//        if (!currentState.canSaveComputed) return
//
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//
//            try {
//                if (templateId == null) {
//                    // Create new template
//                    val result = createPromptTemplateUseCase(
//                        projectId = projectId,
//                        name = currentState.name,
//                        content = currentState.content,
//                        variables = currentState.variables,
//                        commitMessage = "Initial version"
//                    )
//
//                    result.fold(
//                        onSuccess = { template ->
//                            _uiState.update {
//                                it.copy(
//                                    isLoading = false,
//                                    error = null,
//                                    savedSuccessfully = true
//                                )
//                            }
//                        },
//                        onFailure = { exception ->
//                            _uiState.update {
//                                it.copy(
//                                    isLoading = false,
//                                    error = exception.message ?: "Failed to save template"
//                                )
//                            }
//                        }
//                    )
//                } else {
//                    // Update existing template
//                    val existingTemplate = promptTemplateRepository.getById(templateId)
//                    if (existingTemplate != null) {
//                        val updatedTemplate = existingTemplate.copy(
//                            name = currentState.name,
//                            content = currentState.content,
//                            variables = currentState.variables,
//                            updatedAt = Clock.System.now()
//                        )
//
//                        promptTemplateRepository.update(updatedTemplate)
//
//                        _uiState.update {
//                            it.copy(
//                                isLoading = false,
//                                error = null,
//                                savedSuccessfully = true
//                            )
//                        }
//                    } else {
//                        _uiState.update {
//                            it.copy(
//                                isLoading = false,
//                                error = "Template not found"
//                            )
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        error = e.message ?: "Failed to save template"
//                    )
//                }
//            }
//        }
//    }

    fun getPreview(): String {
//        var preview = _uiState.value.content
//        _uiState.value.previewValues.forEach { (name, value) ->
//            preview = preview.replace("{{$name}}", value)
//        }
//        return preview
        return ""
    }
}
