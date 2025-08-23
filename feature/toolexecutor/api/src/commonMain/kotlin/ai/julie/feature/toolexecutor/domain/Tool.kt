package ai.julie.feature.toolexecutor.domain

/**
 * Result of a tool execution
 */
sealed interface ToolResult {
    data class Success(
        val content: String,
        val metadata: Map<String, Any> = emptyMap()
    ) : ToolResult
    
    data class Error(
        val message: String,
        val code: String? = null,
        val throwable: Throwable? = null
    ) : ToolResult
}

/**
 * Base interface for all tools that can be called by the model
 */
interface Tool {
    /**
     * Unique identifier for this tool
     */
    val name: String
    
    /**
     * Human-readable description of what this tool does
     */
    val description: String
    
    /**
     * Schema describing the parameters this tool accepts
     * Format can vary (JSON Schema, custom format, etc.)
     */
    val parameterSchema: String
    
    /**
     * Execute this tool with the given parameters
     * @param parameters Map of parameter names to values
     * @return Result of the tool execution
     */
    suspend fun execute(parameters: Map<String, Any>): ToolResult
}