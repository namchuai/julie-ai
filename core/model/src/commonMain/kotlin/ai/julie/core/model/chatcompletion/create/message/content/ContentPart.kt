package ai.julie.core.model.chatcompletion.create.message.content

import kotlinx.serialization.Serializable

/**
 * Represents a part of the message content, which can be text, image, audio, or file data.
 * Used when the message content is an array of parts.
 */
@Serializable
sealed interface ContentPart {
    val type: String
}