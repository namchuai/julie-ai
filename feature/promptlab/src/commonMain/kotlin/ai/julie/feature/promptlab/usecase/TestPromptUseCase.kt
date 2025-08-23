package ai.julie.feature.promptlab.usecase

import ai.julie.feature.promptlab.model.TestResult
import ai.julie.feature.promptlab.model.TestSession
import ai.julie.feature.promptlab.model.provider.PromptLabModelProvider
import ai.julie.feature.promptlab.repository.PromptTemplateRepository
import ai.julie.feature.promptlab.repository.TestSessionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlin.uuid.Uuid

class TestPromptUseCase(
    private val promptTemplateRepository: PromptTemplateRepository,
    private val testSessionRepository: TestSessionRepository,
    private val modelProvider: PromptLabModelProvider
) {
    suspend operator fun invoke(
        templateId: String,
        versionId: String,
        models: List<String>,
        variableValues: Map<String, String>,
        sessionName: String,
        evaluationCriteria: List<String> = emptyList()
    ): Result<TestSession> = runCatching {
        // Get the prompt version
        val version = promptTemplateRepository.getVersion(versionId)
            ?: throw IllegalArgumentException("Version not found")

        // Replace variables in prompt
        val finalPrompt = replaceVariables(version.content, variableValues)

        // Test with all models in parallel
        val testResults = coroutineScope {
            models.map { modelId ->
                async {
                    testWithModel(modelId, finalPrompt, evaluationCriteria)
                }
            }.awaitAll()
        }

        // Create and save test session
        val session = TestSession(
            id = Uuid.random().toHexString(),
            name = sessionName,
            templateId = templateId,
            versionId = versionId,
            models = models,
            variableValues = variableValues,
            testResults = testResults,
            createdAt = Clock.System.now()
        )

        testSessionRepository.create(session)
    }

    private suspend fun testWithModel(
        modelId: String,
        prompt: String,
        evaluationCriteria: List<String>
    ): TestResult {
        // Get available models and find the one matching modelId
        val availableModels = modelProvider.getAvailableModels()
        val model = availableModels.find { it.id == modelId }
            ?: throw IllegalArgumentException("Model $modelId not found")

        val result = modelProvider.generateResponse(model, prompt)

        // TODO: Implement evaluation logic
        val evaluations = evaluationCriteria.associateWith { 0.0 }

        return TestResult(
            modelId = modelId,
            response = result.response,
            latencyMs = result.latencyMs,
            tokenCount = result.tokenCount,
            evaluations = evaluations
        )
    }

    private fun replaceVariables(content: String, values: Map<String, String>): String {
        var result = content
        values.forEach { (key, value) ->
            result = result.replace("{{$key}}", value)
        }
        return result
    }
}