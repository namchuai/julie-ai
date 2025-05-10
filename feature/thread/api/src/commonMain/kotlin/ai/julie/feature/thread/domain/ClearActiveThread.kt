package ai.julie.feature.thread.domain

/**
 * Functional interface for clearing the active thread.
 */
fun interface ClearActiveThread {
    suspend fun clearActiveThread()
}