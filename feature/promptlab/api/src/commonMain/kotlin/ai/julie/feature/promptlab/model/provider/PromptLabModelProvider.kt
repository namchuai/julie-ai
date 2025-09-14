package ai.julie.feature.promptlab.model.provider

import ai.julie.core.model.aimodel.AiModel

interface PromptLabModelProvider {
    suspend fun generateResponse(model: AiModel, prompt: String): ModelResponse
    suspend fun getAvailableModels(): List<AiModel>
}

data class ModelResponse(
    val response: String,
    val tokenCount: Int,
    val latencyMs: Long = 0
)