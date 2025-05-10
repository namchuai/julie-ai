package ai.julie.feature.thread.domain

/**
 * Functional interface for updating a thread's timestamp.
 */
fun interface UpdateThreadTimestamp {
    suspend fun updateThreadTimestamp(threadId: String)
}