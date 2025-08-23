package ai.julie.feature.toolmanagement.domain

/**
 * Functional interface for updating a tool.
 */
fun interface UpdateTool {
    suspend fun updateTool(tool: EnrichedTool)
}