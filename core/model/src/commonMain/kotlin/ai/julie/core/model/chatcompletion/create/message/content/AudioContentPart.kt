package ai.julie.core.model.chatcompletion.create.message.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an audio content part.
 */
@Serializable
@SerialName("input_audio")
data class AudioContentPart(
    @SerialName("input_audio") val inputAudio: InputAudio,
    // The API docs structure nests the audio data but specifies the top-level type is always "input_audio".
    override val type: String = "input_audio"
) : ContentPart {
    /**
     * Contains the base64 encoded audio data and its format.
     */
     @Serializable
     data class InputAudio(
         /** Base64 encoded audio data. */
         val data: String,
         /** The format of the encoded audio data (e.g., "wav", "mp3"). */
         val format: String
     )
} 