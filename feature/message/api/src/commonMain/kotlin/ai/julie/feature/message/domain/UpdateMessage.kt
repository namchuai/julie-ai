package ai.julie.feature.message.domain

import ai.julie.feature.message.domain.model.EnrichedMessage

/**
 * Functional interface for updating an existing message.
 */
fun interface UpdateMessage {
    suspend fun updateMessage(
        messageId: String,
        content: String
    ): EnrichedMessage
}