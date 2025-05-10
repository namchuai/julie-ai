package ai.julie.panel.chat

import ai.julie.feature.thread.domain.model.EnrichedThread

data class ChatPanelState(
    val activeThread: EnrichedThread?,
    val threads: List<EnrichedThread>,
)
