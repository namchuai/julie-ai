package ai.julie.feature.thread.domain

import ai.julie.feature.thread.domain.model.EnrichedThread
import kotlinx.coroutines.flow.Flow

/**
 * Functional interface for getting a flow of the active thread.
 */
fun interface FlowOfActiveThread {
    fun flowOfActiveThread(): Flow<EnrichedThread?>
}