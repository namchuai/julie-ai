package ai.julie.feature.promptlab.model.provider

import ai.julie.core.model.aimodel.AiModel
import ai.julie.core.model.aimodel.RemoteModel

class PromptLabModelProviderImpl : PromptLabModelProvider {

    override suspend fun generateResponse(model: AiModel, prompt: String): ModelResponse {
        var response = ""
        var tokenCount = 0

        return ModelResponse(
            response = response,
            tokenCount = tokenCount,
            latencyMs = 1 //duration.inWholeMilliseconds
        )
    }

    override suspend fun getAvailableModels(): List<AiModel> {
        // TODO: Get from model management service
        return listOf(
            RemoteModel(
                id = "gpt-4",
                title = "GPT-4",
                provider = "openai"
            ),
            RemoteModel(
                id = "claude-3",
                title = "Claude 3",
                provider = "anthropic"
            )
        )
    }
}
