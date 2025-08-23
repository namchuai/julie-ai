package ai.julie.feature.jinjaparser.domain

import ai.julie.feature.message.domain.model.EnrichedMessage

fun interface ParseConversation {
    fun parseConversation(
        messages: List<EnrichedMessage>,
        context: Map<String, Any>
    )
}
