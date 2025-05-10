package ai.julie.feature.message.domain.model

import com.aallam.openai.api.core.Role
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent

data class EnrichedMessage(
    val message: Message,
) {
    val id get() = message.id.id
    val content get() = message.content
    val role
        get() = when (message.role) {
            Role.System -> EnrichedRole.System
            Role.User -> EnrichedRole.User
            Role.Assistant -> EnrichedRole.Assistant
            Role.Function -> EnrichedRole.Function
            Role.Tool -> EnrichedRole.Tool
            else -> throw IllegalArgumentException("Unknown role: ${message.role.role}")
        }
    val createdAt get() = message.createdAt
    val metadata get() = message.metadata
    val assistantId get() = message.assistantId
    val threadId get() = message.threadId
    val runId get() = message.runId
}

// TODO: This is deprecated
fun EnrichedMessage.extractTextContent() = content.firstOrNull()?.let {
    when (it) {
        is MessageContent.Text -> it.text.value
        else -> ""
    }
} ?: ""

