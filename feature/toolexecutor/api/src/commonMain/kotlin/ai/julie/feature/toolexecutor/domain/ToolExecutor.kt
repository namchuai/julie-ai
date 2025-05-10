package ai.julie.feature.toolexecutor.domain

/**
 * Executes tool calls using the registered tools
 */
interface ToolExecutor {
    /**
     * Execute a single tool call
     * @param toolCall The parsed tool call to execute
     * @return Result of the tool execution
     */
    suspend fun execute(toolCall: ToolCall): ToolResult
    
    /**
     * Execute multiple tool calls in sequence
     * @param toolCalls List of tool calls to execute
     * @return List of results in the same order as the input
     */
    suspend fun executeAll(toolCalls: List<ToolCall>): List<ToolResult>
}

/**
 * Simplified executor for single tool execution
 */
fun interface SimpleToolExecutor {
    suspend fun execute(toolCall: ToolCall): ToolResult
}