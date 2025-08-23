package ai.julie.feature.toolmanagement.domain

/**
 * Functional interface for deleting a tool.
 */
fun interface DeleteTool {
    suspend fun deleteTool(toolId: String)
}