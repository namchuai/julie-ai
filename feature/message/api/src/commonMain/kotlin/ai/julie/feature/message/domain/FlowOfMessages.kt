package ai.julie.feature.message.domain

import ai.julie.feature.message.domain.model.EnrichedMessage
import kotlinx.coroutines.flow.Flow

/**
 * Functional interface for getting a flow of messages for a specific thread.
 */
fun interface FlowOfMessages {
    fun flowOfMessages(threadId: String): Flow<List<EnrichedMessage>>
}