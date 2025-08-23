package ai.julie.feature.toolmanagement.domain

import kotlinx.coroutines.flow.Flow

/**
 * Functional interface for getting a flow of all tools.
 */
fun interface FlowOfTools {
    fun flowOfTools(): Flow<List<EnrichedTool>>
}