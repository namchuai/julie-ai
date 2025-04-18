package ai.julie.core.model.chatcompletion.create.message.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a text content part.
 */
@Serializable
@SerialName("text")
data class TextContentPart(
    val text: String,
    override val type: String = "text"
) : ContentPart 