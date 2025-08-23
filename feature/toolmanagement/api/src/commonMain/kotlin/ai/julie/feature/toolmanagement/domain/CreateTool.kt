package ai.julie.feature.toolmanagement.domain

/**
 * Functional interface for creating a new tool.
 */
fun interface CreateTool {
    suspend fun createTool(tool: EnrichedTool): String
}