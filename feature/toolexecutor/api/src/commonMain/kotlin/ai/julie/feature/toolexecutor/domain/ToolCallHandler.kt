package ai.julie.feature.toolexecutor.domain

/**
 * Main orchestrator for handling tool calls from model output
 * Coordinates parsing, execution, and formatting
 */
interface ToolCallHandler {
    /**
     * Process model output and handle any tool calls found
     * Returns null if no tool calls were found, otherwise returns formatted response
     * 
     * @param modelOutput The raw output from the language model
     * @return Formatted tool response to append to context, or null if no tools called
     */
    suspend fun handleModelOutput(modelOutput: String): String?
    
    /**
     * Check if model output contains tool calls without executing them
     * @param modelOutput The raw output from the language model
     * @return True if tool calls are detected
     */
    suspend fun hasToolCalls(modelOutput: String): Boolean
}