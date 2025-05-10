package ai.julie.feature.message.domain

/**
 * Functional interface for deleting a message.
 */
fun interface DeleteMessage {
    suspend fun deleteMessage(messageId: String)
}