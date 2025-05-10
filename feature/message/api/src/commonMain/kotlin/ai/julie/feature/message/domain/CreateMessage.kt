package ai.julie.feature.message.domain

import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.message.domain.model.EnrichedRole

/**
 * Functional interface for creating a new message.
 */
fun interface CreateMessage {
    suspend fun createMessage(
        threadId: String,
        content: String,
        role: EnrichedRole
    ): EnrichedMessage
}