package ai.julie.feature.chat.domain

// import ai.julie.feature.jinjaparser.domain.ProcessChatTemplate
import ai.julie.feature.message.domain.CreateMessage
import ai.julie.feature.message.domain.FlowOfMessages
import ai.julie.feature.message.domain.UpdateMessage
import ai.julie.feature.message.domain.model.EnrichedRole
import ai.julie.feature.modelconfig.domain.FlowOfModelMetadata
import ai.julie.feature.modelconfig.domain.gguf.LlamaModelMetadata
import ai.julie.feature.modelconfig.domain.gguf.deepseek2.Deepseek2ModelMetadata
import ai.julie.feature.modelconfig.domain.gguf.qwen3.Qwen3MoeModelMetadata
import ai.julie.feature.modelconfig.domain.preset.SamplingPreset
import ai.julie.feature.modelmanagement.data.ModelExecutionRepository
import ai.julie.feature.modelmanagement.domain.FlowOfLocalModels
import ai.julie.feature.toolexecutor.data.BraveSearchService
import ai.julie.logging.Logger
import kotlinx.coroutines.flow.firstOrNull

/**
 * Simplified inference implementation using the new orchestrator approach
 */
class SimpleInferenceUseCase(
    private val flowOfMessages: FlowOfMessages,
    private val createMessage: CreateMessage,
    private val updateMessage: UpdateMessage,
    private val flowOfModelMetadata: FlowOfModelMetadata,
    private val flowOfLocalModels: FlowOfLocalModels,
    private val modelExecutionRepository: ModelExecutionRepository,
    private val braveSearchService: BraveSearchService,
    // private val processChatTemplate: ProcessChatTemplate,
) {
    suspend fun invoke(
        threadId: String,
        modelId: String,
        samplingPreset: SamplingPreset,
    ) {
        Logger.d("SimpleInferenceUseCase: Starting inference for thread $threadId")
        
        // Get messages and model metadata
        val messages = flowOfMessages.flowOfMessages(threadId).firstOrNull() ?: emptyList()
        if (messages.isEmpty()) {
            Logger.e("No messages found for thread ID: $threadId")
            return
        }
        
        val modelMetadata = flowOfModelMetadata.flowOfModelMetadata(modelId).firstOrNull()
        require(modelMetadata != null) { "Model metadata not found for model ID: $modelId" }
        
        // Get local model
        val localModels = flowOfLocalModels.flowOfLocalModels().firstOrNull() ?: emptyList()
        val localModel = localModels.find { it.id == modelId }
        require(localModel != null) { "Local model not found for model ID: $modelId" }
        
        // Get model execution instance
        val modelExecution = modelExecutionRepository.requestModelExecution(threadId, localModel)
        require(modelExecution != null) { "Failed to get model execution for model ID: $modelId" }
        
        // Create assistant message
        val assistantMessage = createMessage.createMessage(
            threadId = threadId,
            content = "",
            role = EnrichedRole.Assistant
        )
        
        try {
            // Build a simple prompt for now (without processing through JinjaParser)
            val processedPrompt = buildSimplePrompt(messages, modelMetadata)
            
            Logger.d("Processed prompt length: ${processedPrompt.length}")
            
            // Create orchestrator and start generation
            val llamaBinding = (modelExecution as ai.julie.core.data.llama.LlamaRepository).llamaBinding
            require(llamaBinding != null) { "LlamaBinding not initialized in repository" }
            
            // Clear any existing context to ensure clean state
            Logger.d("Clearing context for clean inference...")
            llamaBinding.clearKvCache(seqId = 0, start = -1, end = -1)
            
            val orchestrator = InferenceOrchestrator(
                llamaBinding = llamaBinding,
                braveSearchService = braveSearchService
            )
            
            var accumulatedText = ""
            
            orchestrator.generateWithTools(
                prompt = processedPrompt,
                samplerSettings = samplingPreset.toLlamaSamplerSettings(),
                maxTokens = 1000
            ).collect { result ->
                when (result) {
                    is GenerationResult.TextChunk -> {
                        accumulatedText += result.text
                        updateMessage.updateMessage(
                            messageId = assistantMessage.id,
                            content = accumulatedText
                        )
                    }
                    
                    is GenerationResult.FunctionCallDetected -> {
                        updateMessage.updateMessage(
                            messageId = assistantMessage.id,
                            content = "🔍 Searching the web..."
                        )
                        Logger.d("Function call detected: ${result.functionCall}")
                    }
                    
                    is GenerationResult.FunctionResult -> {
                        // Show the search results in a formatted way
                        updateMessage.updateMessage(
                            messageId = assistantMessage.id,
                            content = formatSearchResults(result.result)
                        )
                        Logger.d("Function result displayed: ${result.result}")
                    }
                    
                    is GenerationResult.Completed -> {
                        Logger.d("Generation completed: ${result.finalText}")
                    }
                    
                    is GenerationResult.Error -> {
                        Logger.e("Generation error: ${result.message}")
                        updateMessage.updateMessage(
                            messageId = assistantMessage.id,
                            content = "Error: ${result.message}"
                        )
                    }
                }
            }
            
        } catch (e: Exception) {
            Logger.e("Error during inference: ${e.message}")
            updateMessage.updateMessage(
                messageId = assistantMessage.id,
                content = "Error: ${e.message}"
            )
        }
    }
    
    private fun getChatTemplate(modelMetadata: Any): String {
        return when (modelMetadata) {
            is LlamaModelMetadata -> modelMetadata.chatTemplate?.value ?: getDefaultTemplate()
            is Qwen3MoeModelMetadata -> modelMetadata.chatTemplate?.value ?: getDefaultTemplate()
            is Deepseek2ModelMetadata -> modelMetadata.chatTemplate?.value ?: getDefaultTemplate()
            else -> getDefaultTemplate()
        }
    }
    
    private fun getBosToken(modelMetadata: Any): String? {
        return when (modelMetadata) {
            is LlamaModelMetadata -> modelMetadata.bosToken?.value
            is Qwen3MoeModelMetadata -> modelMetadata.bosToken?.value
            is Deepseek2ModelMetadata -> modelMetadata.bosToken?.value
            else -> null
        }
    }
    
    private fun getEosToken(modelMetadata: Any): String? {
        return when (modelMetadata) {
            is LlamaModelMetadata -> modelMetadata.eosToken?.value
            is Qwen3MoeModelMetadata -> modelMetadata.eosToken?.value
            is Deepseek2ModelMetadata -> modelMetadata.eosToken?.value
            else -> null
        }
    }
    
    private fun getDefaultTemplate(): String {
        return """<|begin_of_text|>{% for message in messages %}{% if message.role == 'user' %}<|start_header_id|>user<|end_header_id|>

{{ message.content }}<|eot_id|>{% elif message.role == 'assistant' %}<|start_header_id|>assistant<|end_header_id|>

{{ message.content }}<|eot_id|>{% endif %}{% endfor %}<|start_header_id|>user<|end_header_id|>

{{ current_message }}<|eot_id|><|start_header_id|>assistant<|end_header_id|>

"""
    }
    
    private fun buildSimplePrompt(messages: List<Any>, modelMetadata: Any): String {
        // Get the last user message
        val lastUserMessage = messages.lastOrNull { message ->
            // This is a simplified check - in a real implementation, we'd properly parse the message type
            message.toString().contains("User") || message.toString().contains("user")
        }?.toString() ?: "tell me what's the latest in AI development"
        
        // Extract just the content part from the message (simplified)
        val messageContent = if (lastUserMessage.contains("content=")) {
            val contentStart = lastUserMessage.indexOf("content=") + "content=".length
            val contentEnd = lastUserMessage.indexOf(",", contentStart).takeIf { it != -1 } ?: lastUserMessage.length
            lastUserMessage.substring(contentStart, contentEnd).trim()
        } else {
            "tell me what's the latest in AI development"
        }
        
        return buildString {
            append("<|begin_of_text|>")
            append("<|start_header_id|>system<|end_header_id|>\n\n")
            append("You are a helpful assistant. Always search for current information using the brave_search function before answering questions.\n\n<|eot_id|>\n")
            append("<|start_header_id|>user<|end_header_id|>\n\n")
            append("$messageContent<|eot_id|>\n")
            append("<|start_header_id|>assistant<|end_header_id|>\n\n")
            append("I'll search for current information about that.\n\n")
        }
    }
    
    private fun formatSearchResults(rawResults: String): String {
        // Extract key information from the search results and format nicely
        return if (rawResults.contains("Search Results:")) {
            // Parse the search results and create a more natural response
            val lines = rawResults.split("\n").filter { it.isNotBlank() }
            val resultsLines = lines.drop(1) // Skip "Search Results:" header
            
            // Try to extract the most relevant information from the first result
            if (resultsLines.isNotEmpty()) {
                val firstResult = resultsLines.firstOrNull { it.startsWith("- ") }?.removePrefix("- ")
                if (firstResult != null) {
                    // Create a more natural response based on the search results
                    "Based on current search results: $firstResult\n\n" +
                    "Additional sources available:\n" + 
                    resultsLines.take(3).joinToString("\n") { "• $it" }
                } else {
                    rawResults
                }
            } else {
                rawResults
            }
        } else {
            rawResults
        }
    }
    
    private fun SamplingPreset.toLlamaSamplerSettings(): ai.julie.core.model.LlamaSamplerSettings {
        return ai.julie.core.model.LlamaSamplerSettings(
            temperature = this.temperature.value,
            topP = this.topP.value,
            topK = this.topK.value,
            stop = emptyList() // Let model handle its own stop tokens
        )
    }
}