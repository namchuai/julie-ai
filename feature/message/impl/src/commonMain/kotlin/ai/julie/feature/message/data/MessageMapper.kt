package ai.julie.feature.message.data

import ai.julie.feature.message.domain.model.EnrichedMessage
import ai.julie.feature.message.domain.model.extractTextContent
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageId
import com.aallam.openai.api.message.TextContent
import com.aallam.openai.api.run.RunId
import com.aallam.openai.api.thread.ThreadId
import kotbase.Document
import kotbase.MutableArray
import kotbase.MutableDocument

fun EnrichedMessage.toDocument() = MutableDocument(id).apply {
    setString("id", id)
    setString("threadId", threadId.id)
    setString("role", role.value)

    val contentArray = MutableArray()
    content.forEach { messageContent ->
        when (messageContent) {
            is MessageContent.Text -> {
                contentArray.addString(value = extractTextContent()) // TODO: need to taking care of annotations
            }

            else -> {} // ignore for now
        }
    }
    setArray("content", contentArray)
    setInt("createdAt", createdAt)
    metadata.forEach { (key, value) ->
        setString("metadata.$key", value)
    }
    assistantId?.let { setString("assistantId", it.id) }
    runId?.let { setString("runId", it.id) }
}

fun Document.toEnrichedMessage(): EnrichedMessage {
    val messageId = getString("id") ?: throw IllegalArgumentException("Message ID is missing")
    val threadId = getString("threadId") ?: throw IllegalArgumentException("Thread ID is missing")
    val content: List<MessageContent> = getArray("content")?.toList()?.map { contentItem ->
        MessageContent.Text(
            text = TextContent(
                value = contentItem.toString(),
                annotations = emptyList()
            )
        )
    } ?: emptyList()

    return EnrichedMessage(
        message = Message(
            id = MessageId(messageId),
            createdAt = getInt("createdAt"),
            threadId = ThreadId(threadId),
            role = Role(getString("role") ?: ""),
            content = content,
            metadata = keys.filter { it.startsWith("metadata.") }
                .associate {
                    it.removePrefix("metadata.") to (getString(it) ?: "")
                },
            assistantId = getString(key = "assistantId")?.let { AssistantId(id = it) },
            runId = getString(key = "runId")?.let { RunId(id = it) }
        )
    )
}