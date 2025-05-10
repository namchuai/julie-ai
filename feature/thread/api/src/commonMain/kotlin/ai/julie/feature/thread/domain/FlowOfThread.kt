package ai.julie.feature.thread.domain

import ai.julie.feature.thread.domain.model.EnrichedThread
import kotlinx.coroutines.flow.Flow

fun interface FlowOfThread {
    fun flowOfThread(threadId: String): Flow<EnrichedThread?>
}