package ai.julie.feature.thread.domain

import ai.julie.feature.thread.domain.model.EnrichedThread

/**
 * Functional interface for creating a new thread.
 */
fun interface CreateThread {
    suspend fun createThread(
        modelId: String,
        title: String?
    ): EnrichedThread
}