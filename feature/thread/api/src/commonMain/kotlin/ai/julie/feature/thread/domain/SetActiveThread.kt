package ai.julie.feature.thread.domain

/**
 * Functional interface for setting the active thread.
 */
fun interface SetActiveThread {
    fun setActiveThreadId(threadId: String)
}