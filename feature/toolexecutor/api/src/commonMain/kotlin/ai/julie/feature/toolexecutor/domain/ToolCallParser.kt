package ai.julie.feature.toolexecutor.domain

/**
 * Represents a parsed tool call from model output
 */
data class ToolCall(
    val toolName: String,
    val methodName: String = "call",
    val parameters: Map<String, Any> = emptyMap(),
    val rawCall: String = ""
)

/**
 * Parses tool calls from model output in various formats
 * Primary format: <|python_tag|>tool_name.method(param="value")<|eom_id|>
 */
fun interface ToolCallParser {
    /**
     * Parse model output and extract tool calls
     * @param modelOutput The raw output from the language model
     * @return List of parsed tool calls
     */
    suspend fun parse(modelOutput: String): List<ToolCall>
}