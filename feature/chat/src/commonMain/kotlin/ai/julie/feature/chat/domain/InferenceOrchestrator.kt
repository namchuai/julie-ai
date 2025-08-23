package ai.julie.feature.chat.domain

import ai.julie.core.model.LlamaSamplerSettings
import ai.julie.feature.toolexecutor.data.BraveSearchService
import ai.julie.llamabinding.LlamaBinding
import ai.julie.logging.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * Simple inference orchestrator that handles function calling.
 * Uses the thin LlamaBinding wrapper to control generation step-by-step.
 */
class InferenceOrchestrator(
    private val llamaBinding: LlamaBinding,
    private val braveSearchService: BraveSearchService,
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    private val TAG = "InferenceOrchestrator"

    /**
     * Generate text with function calling support.
     * Returns a flow of text chunks.
     */
    fun generateWithTools(
        prompt: String,
        samplerSettings: LlamaSamplerSettings,
        maxTokens: Int = 1000
    ): Flow<GenerationResult> = flow {
        Logger.d("[$TAG] Starting generation with prompt length: ${prompt.length}")
        
        // Initialize generation
        val tokens = llamaBinding.tokenize(prompt, llamaBinding.getContextSize())
        val batch = llamaBinding.createBatch(llamaBinding.getContextSize())
        
        try {
            // Setup batch with prompt tokens
            llamaBinding.clearBatch(batch)
            for (i in tokens.indices) {
                llamaBinding.setBatchToken(batch, i, tokens[i])
                llamaBinding.setBatchPosition(batch, i, i)
                llamaBinding.setBatchSequenceId(batch, i, 0)
                llamaBinding.setBatchLogits(batch, i, i == tokens.size - 1)
            }
            llamaBinding.setBatchSize(batch, tokens.size)
            
            // Decode prompt
            val decodeResult = llamaBinding.decode(batch)
            if (decodeResult != 0) {
                emit(GenerationResult.Error("Failed to decode prompt"))
                return@flow
            }
            
            Logger.d("[$TAG] Prompt decoded successfully, starting generation")
            
            var nEvaluated = tokens.size
            var generatedText = StringBuilder()
            var tokensGenerated = 0
            
            // Generation loop
            while (tokensGenerated < maxTokens) {
                // Get logits and sample next token
                val logits = llamaBinding.getLogits(if (nEvaluated == tokens.size) nEvaluated - 1 else 0)
                if (logits == null) {
                    Logger.e("[$TAG] Failed to get logits")
                    break
                }
                
                val tokenId = llamaBinding.sampleNextToken(logits, samplerSettings)
                if (tokenId == -1 || tokenId == llamaBinding.getEosToken()) {
                    Logger.d("[$TAG] Generation stopped (EOS or sampling failed)")
                    break
                }
                
                // Convert token to text
                val tokenText = llamaBinding.tokenToText(tokenId)
                generatedText.append(tokenText)
                
                Logger.d("[$TAG] Generated token: $tokenId -> '$tokenText'")
                
                // Check for complete function call
                val currentText = generatedText.toString()
                val functionCall = detectFunctionCall(currentText)
                
                if (functionCall != null) {
                    Logger.d("[$TAG] Function call detected: $functionCall")
                    emit(GenerationResult.FunctionCallDetected(functionCall))
                    
                    // Execute the function call
                    val toolResult = executeFunction(functionCall)
                    emit(GenerationResult.FunctionResult(toolResult))
                    
                    // Complete the generation after tool execution
                    emit(GenerationResult.Completed(currentText))
                    break
                }
                
                // Emit the generated text chunk
                emit(GenerationResult.TextChunk(tokenText))
                
                // Prepare next iteration
                llamaBinding.clearBatch(batch)
                llamaBinding.setBatchToken(batch, 0, tokenId)
                llamaBinding.setBatchPosition(batch, 0, nEvaluated)
                llamaBinding.setBatchSequenceId(batch, 0, 0)
                llamaBinding.setBatchLogits(batch, 0, true)
                llamaBinding.setBatchSize(batch, 1)
                
                val nextDecodeResult = llamaBinding.decode(batch)
                if (nextDecodeResult != 0) {
                    Logger.e("[$TAG] Failed to decode token")
                    break
                }
                
                nEvaluated++
                tokensGenerated++
            }
            
            if (tokensGenerated >= maxTokens) {
                emit(GenerationResult.Completed("Maximum tokens reached"))
            }
            
        } finally {
            llamaBinding.freeBatch(batch)
        }
    }
    
    /**
     * Detect if the generated text contains a complete function call
     */
    private fun detectFunctionCall(text: String): String? {
        Logger.d("[$TAG] Checking for function calls in text: '$text'")
        
        // Pattern 1: Direct call format - brave_search.call(query="...")
        val directPattern = """((?:brave_search|brute_force_search)\.call\(query="([^"]+)"\))""".toRegex()
        val directMatch = directPattern.find(text)
        if (directMatch != null) {
            val functionCall = directMatch.groupValues[1]
            Logger.d("[$TAG] ✅ Direct function call detected: '$functionCall'")
            return functionCall
        }
        
        // Pattern 2: JSON function call format - {"name": "brave_search", "parameters": {"query": "..."}}
        if (text.contains("{") && text.contains("\"name\"")) {
            val startIndex = text.indexOf("{")
            if (startIndex >= 0) {
                val afterStart = text.substring(startIndex)
                val openBraces = afterStart.count { it == '{' }
                val closeBraces = afterStart.count { it == '}' }
                
                // Check if we have a complete JSON object
                if (openBraces > 0 && openBraces == closeBraces && 
                    afterStart.contains("\"name\"") && 
                    afterStart.contains("\"parameters\"") &&
                    afterStart.contains("\"query\"")) {
                    
                    val endIndex = afterStart.lastIndexOf("}") + 1
                    val functionCall = afterStart.substring(0, endIndex)
                    Logger.d("[$TAG] ✅ JSON function call detected: '$functionCall'")
                    return functionCall
                }
            }
        }
        
        Logger.d("[$TAG] ❌ No function call detected")
        return null
    }
    
    /**
     * Execute the detected function call
     */
    private suspend fun executeFunction(functionCall: String): String {
        return try {
            Logger.d("[$TAG] Executing function call: $functionCall")
            val results = braveSearchService.executeToolCall(functionCall)
            
            if (results.isNotEmpty()) {
                buildString {
                    appendLine("Search Results:")
                    results.take(3).forEach { result ->
                        appendLine("- ${result.title}")
                        appendLine("  ${result.description}")
                        appendLine("  ${result.url}")
                        appendLine()
                    }
                }
            } else {
                "No search results found."
            }
        } catch (e: Exception) {
            Logger.e("[$TAG] Error executing function: ${e.message}")
            "Error executing search: ${e.message}"
        }
    }
}

/**
 * Results from the generation process
 */
sealed class GenerationResult {
    data class TextChunk(val text: String) : GenerationResult()
    data class FunctionCallDetected(val functionCall: String) : GenerationResult()
    data class FunctionResult(val result: String) : GenerationResult()
    data class Completed(val finalText: String) : GenerationResult()
    data class Error(val message: String) : GenerationResult()
}