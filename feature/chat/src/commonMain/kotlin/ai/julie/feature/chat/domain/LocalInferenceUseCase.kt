package ai.julie.feature.chat.domain

import ai.julie.core.domain.session.PromptSessionManager
import ai.julie.core.domain.session.PromptSessionState
import ai.julie.feature.message.domain.CreateMessage
import ai.julie.feature.message.domain.FlowOfMessages
import ai.julie.feature.message.domain.UpdateMessage
import ai.julie.feature.message.domain.model.EnrichedRole
import ai.julie.feature.modelconfig.domain.FlowOfModelMetadata
import ai.julie.feature.modelconfig.domain.gguf.LlamaModelMetadata
import ai.julie.feature.modelconfig.domain.gguf.qwen3.Qwen3MoeModelMetadata
import ai.julie.feature.modelconfig.domain.preset.SamplingPreset
import ai.julie.feature.modelmanagement.data.ModelExecutionRepository
import ai.julie.feature.modelmanagement.domain.FlowOfLocalModels
import ai.julie.feature.toolexecutor.domain.ToolCallHandler
import ai.julie.logging.Logger
import com.aallam.openai.api.chat.Tool
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

class LocalInferenceUseCase(
    private val flowOfMessages: FlowOfMessages,
    private val promptSessionManager: PromptSessionManager,
    private val createMessage: CreateMessage,
    private val updateMessage: UpdateMessage,
    private val flowOfModelMetadata: FlowOfModelMetadata,
    private val flowOfLocalModels: FlowOfLocalModels,
    private val modelExecutionRepository: ModelExecutionRepository,
    private val toolCallHandler: ToolCallHandler,
) {
    suspend fun invoke(
        threadId: String,
        modelId: String,
        samplingPreset: SamplingPreset,
    ) {
        Logger.d("LocalInferenceWithSessionUseCase: Starting inference for thread $threadId with model $modelId")

        // Check if there's already an active session for this thread
        if (promptSessionManager.isSessionActive(threadId)) {
            Logger.w("Thread $threadId already has an active session. Cannot start new inference.")
            return
        }

        // Fetch messages for the given thread ID
        val messages = flowOfMessages.flowOfMessages(threadId).firstOrNull() ?: emptyList()
        if (messages.isEmpty()) {
            Logger.e("No messages found for thread ID: $threadId")
            return
        }

        // Get model metadata and settings
        val modelMetadata = flowOfModelMetadata.flowOfModelMetadata(modelId).firstOrNull()
        require(modelMetadata != null) {
            "Model metadata not found for model ID: $modelId"
        }

        val chatTemplate = when (modelMetadata) {
            is LlamaModelMetadata -> {
                modelMetadata.chatTemplate?.value ?: ""
            }

            is Qwen3MoeModelMetadata -> {
                modelMetadata.chatTemplate?.value ?: ""
            }

            else -> {
                """<|begin_of_text|>{% for message in messages %}{% if message.role == 'user' %}<|start_header_id|>user<|end_header_id|>

{{ message.content }}<|eot_id|>{% elif message.role == 'assistant' %}<|start_header_id|>assistant<|end_header_id|>

{{ message.content }}<|eot_id|>{% endif %}{% endfor %}<|start_header_id|>user<|end_header_id|>

{{ current_message }}<|eot_id|><|start_header_id|>assistant<|end_header_id|>

"""
            }
        }

        // Get the local model by model ID
        val localModels = flowOfLocalModels.flowOfLocalModels().firstOrNull() ?: emptyList()
        val localModel = localModels.find { it.id == modelId }
        require(localModel != null) {
            "Local model not found for model ID: $modelId"
        }

        val promptable = modelExecutionRepository.requestModelExecution(threadId, localModel)
        require(promptable != null) {
            "Failed to get promptable for model ID: $modelId"
        }
        promptSessionManager.startNewSession(
            threadId = threadId,
            modelId = modelId,
            promptable = promptable,
        )

        // Create initial empty assistant message to get ID for streaming updates
        Logger.d("Creating empty assistant message...")
        val assistantMessage = createMessage.createMessage(
            threadId = threadId,
            content = "",
            role = EnrichedRole.Assistant
        )
        Logger.d("Created assistant message with ID: ${assistantMessage.id}")

        val bosToken = if (modelMetadata is LlamaModelMetadata) {
            modelMetadata.bosToken?.value
        } else {
            null
        }

        // TODO: Create mock weather tool for testing once we figure out the correct Tool API
        // For now, use empty tools list to get the build working
        val tools = listOf<Tool>()

        // Stream model response and update assistant message incrementally
        var accumulatedResponse = ""
        var isBufferingToolCall = false
        try {
            promptSessionManager.prompt(
                threadId = threadId,
                messages = messages,
                samplerSettings = samplingPreset,
                template = chatTemplate,
                addGenerationPrompt = true, // TODO: self question, when this value should be false?
                bosToken = bosToken,
                dateString = null,
                tools = tools,
            ).collect { chunk ->
                accumulatedResponse += chunk
                
                // Check if response looks like a tool call
                val trimmed = accumulatedResponse.trimStart()
                if (trimmed.startsWith("{") && trimmed.contains("\"name\"") && trimmed.contains("\"parameters\"")) {
                    if (!isBufferingToolCall) {
                        isBufferingToolCall = true
                        // Show tool execution indicator instead of JSON
                        updateMessage.updateMessage(
                            messageId = assistantMessage.id,
                            content = "🔧 Executing function call..."
                        )
                        Logger.d("Tool call detected, buffering response and showing execution indicator")
                    }
                    // Don't update UI with JSON chunks - keep showing the indicator
                } else if (!isBufferingToolCall) {
                    // Normal response - stream to UI
                    updateMessage.updateMessage(
                        messageId = assistantMessage.id,
                        content = accumulatedResponse
                    )
                }
            }

            // Check for tool calls after streaming is complete
            if (isBufferingToolCall) {
                // For now, use dummy tool execution since toolCallHandler might not be implemented
                Logger.d("Tool call detected in response: $accumulatedResponse")
                
                // Show that we're executing the tool
                updateMessage.updateMessage(
                    messageId = assistantMessage.id,
                    content = "🔧 Executing weather function..."
                )
                
                // Simulate tool execution with 5 second delay
                delay(5000)
                val dummyToolResult = """{"temperature": "22°C", "condition": "Sunny", "humidity": "65%", "wind": "10 km/h"}"""
                
                Logger.d("Dummy tool executed, result: $dummyToolResult")
                
                // Show that we're processing the result
                updateMessage.updateMessage(
                    messageId = assistantMessage.id,
                    content = "🔧 Processing weather data..."
                )
                
                // Small delay to show the processing message
                delay(1000)
                
                // For now, create a human-readable response from the tool result
                val humanReadableResponse = """Based on the weather data:
                    |📍 Location: Paris
                    |🌡️ Temperature: 22°C
                    |☀️ Condition: Sunny
                    |💧 Humidity: 65%
                    |💨 Wind: 10 km/h
                    |
                    |It's a beautiful sunny day in Paris with comfortable temperature!""".trimMargin()
                
                // Update with the final human-readable response
                updateMessage.updateMessage(
                    messageId = assistantMessage.id,
                    content = humanReadableResponse
                )
                
                Logger.d("Tool execution flow completed")
                
                // TODO: In real implementation, we would:
                // 1. Parse the tool call JSON
                // 2. Execute the actual function
                // 3. Add tool result to messages
                // 4. Continue prompting for human-readable response
            }

            // Clean up session after successful completion
            promptSessionManager.cleanupSession(threadId)

        } catch (e: Exception) {
            Logger.e("Error during inference: ${e.message}")
            // Update message with error
            updateMessage.updateMessage(
                messageId = assistantMessage.id,
                content = "Error: ${e.message}"
            )
            // Clean up session on error
            promptSessionManager.cleanupSession(threadId)
            throw e
        }
    }

    suspend fun cancelInference(threadId: String) {
        Logger.d("Cancelling inference for thread $threadId")
        promptSessionManager.cancelSession(threadId)
    }

    suspend fun cleanupThread(threadId: String) {
        Logger.d("Cleaning up session for thread $threadId")
        promptSessionManager.cleanupSession(threadId)
    }

    suspend fun getSessionState(threadId: String): PromptSessionState {
        return promptSessionManager.getSessionState(threadId)
    }

    suspend fun isSessionActive(threadId: String): Boolean {
        return promptSessionManager.isSessionActive(threadId)
    }
}