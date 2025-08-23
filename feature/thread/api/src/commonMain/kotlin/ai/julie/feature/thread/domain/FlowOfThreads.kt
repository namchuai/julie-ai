package ai.julie.feature.thread.domain

import ai.julie.feature.thread.domain.model.EnrichedThread
import kotlinx.coroutines.flow.Flow

/**
 * Functional interface for getting a flow of threads.
 */
fun interface FlowOfThreads {
    fun flowOfThreads(): Flow<List<EnrichedThread>>
}