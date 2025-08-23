package ai.julie.feature.promptlab.ui.test

import ai.julie.core.model.aimodel.AiModel
import ai.julie.core.model.aimodel.RemoteModel
import ai.julie.feature.promptlab.model.PromptTemplate
import ai.julie.feature.promptlab.model.TestResult
import ai.julie.feature.promptlab.model.VariableConfig
import ai.julie.feature.promptlab.model.VariableType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class TestSessionViewModel(
    private val templateId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestSessionUiState())
    val uiState: StateFlow<TestSessionUiState> = _uiState.asStateFlow()

    init {
        loadTemplate()
        loadAvailableModels()
    }

    private fun loadTemplate() {
        viewModelScope.launch {
            // TODO: Load from repository
            val mockTemplate = PromptTemplate(
                id = templateId,
                projectId = "project1",
                name = "Customer Support Template",
                content = "Hello {{customerName}}, I understand you have a question about {{product}}. How can I help you today?",
                variables = mapOf(
                    "customerName" to VariableConfig(
                        type = VariableType.STRING,
                        description = "Customer's name",
                        defaultValue = "John"
                    ),
                    "product" to VariableConfig(
                        type = VariableType.STRING,
                        description = "Product name",
                        defaultValue = "our service"
                    )
                ),
                currentVersion = 1,
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )

            _uiState.update {
                it.copy(
                    template = mockTemplate,
                    variableValues = mockTemplate.variables.mapValues { (_, config) ->
                        config.defaultValue ?: ""
                    }
                )
            }
        }
    }

    private fun loadAvailableModels() {
        viewModelScope.launch {
            // TODO: Load from model provider
            val mockModels = listOf(
                RemoteModel(
                    id = "gpt-4",
                    title = "GPT-4",
                    provider = "openai"
                ),
                RemoteModel(
                    id = "gpt-3.5-turbo",
                    title = "GPT-3.5 Turbo",
                    provider = "openai"
                ),
                RemoteModel(
                    id = "claude-3",
                    title = "Claude 3",
                    provider = "anthropic"
                )
            )

            _uiState.update {
                it.copy(availableModels = mockModels)
            }
        }
    }

    fun updateVariableValue(name: String, value: String) {
        _uiState.update { state ->
            state.copy(
                variableValues = state.variableValues + (name to value)
            )
        }
    }

    fun selectModel(modelId: String) {
        _uiState.update { state ->
            state.copy(
                selectedModels = state.selectedModels + modelId
            )
        }
    }

    fun deselectModel(modelId: String) {
        _uiState.update { state ->
            state.copy(
                selectedModels = state.selectedModels - modelId
            )
        }
    }

    fun runTest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true) }

            // TODO: Implement actual testing logic
            kotlinx.coroutines.delay(2000) // Simulate API calls

            val mockResults = _uiState.value.selectedModels.map { modelId ->
                TestResult(
                    modelId = modelId,
                    response = "This is a mock response from $modelId for the given prompt.",
                    latencyMs = (500..2000).random().toLong(),
                    tokenCount = (50..200).random(),
                    evaluations = emptyMap()
                )
            }

            _uiState.update {
                it.copy(
                    isRunning = false,
                    testResults = mockResults
                )
            }
        }
    }
}

data class TestSessionUiState(
    val isLoading: Boolean = false,
    val isRunning: Boolean = false,
    val template: PromptTemplate? = null,
    val variableValues: Map<String, String> = emptyMap(),
    val availableModels: List<AiModel> = emptyList(),
    val selectedModels: Set<String> = emptySet(),
    val testResults: List<TestResult> = emptyList(),
    val error: String? = null
)