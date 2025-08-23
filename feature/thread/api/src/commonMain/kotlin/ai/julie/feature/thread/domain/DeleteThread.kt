package ai.julie.feature.thread.domain

/**
 * Functional interface for deleting a thread.
 */
fun interface DeleteThread {
    suspend fun deleteThread(threadId: String)
}