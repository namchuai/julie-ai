package ai.julie.feature.thread.ui.threadlisting

import ai.julie.feature.thread.domain.model.EnrichedThread

data class ThreadListingState(
    val threads: List<EnrichedThread>,
    val activeThread: EnrichedThread? = null,
)