package ai.julie.feature.toolexecutor.domain

/**
 * Formats tool execution results for injection back into the model context
 */
fun interface ToolResponseFormatter {
    /**
     * Format tool results for model consumption
     * Typically wraps results with special tokens like:
     * <|eot_id|><|start_header_id|>ipython<|end_header_id|>
     * {tool_result}
     * <|eot_id|><|start_header_id|>assistant<|end_header_id|>
     * 
     * @param results List of tool execution results
     * @return Formatted string ready to append to model context
     */
    fun format(results: List<ToolResult>): String
}