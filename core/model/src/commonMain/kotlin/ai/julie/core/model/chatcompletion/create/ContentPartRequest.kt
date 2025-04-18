package ai.julie.core.model.chatcompletion.create

import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ImageUrlRequest(
    @SerialName("url") val url: String,
    @SerialName("detail") val detail: String? = null
)

@Serializable
data class InputAudioRequest(
    @SerialName("data") val data: String,
    @SerialName("format") val format: String
)

@Serializable
data class FileDataRequest(
    @SerialName("file_data") val fileData: String? = null,
    @SerialName("file_id") val fileId: String? = null,
    @SerialName("filename") val filename: String? = null
)

@Serializable
sealed class ContentPartRequest {
    abstract val type: String
}

@Serializable
@SerialName("text")
data class TextContentPartRequest(
    @SerialName("text") val text: String
) : ContentPartRequest() {
    @Required
    @SerialName("type")
    override val type: String = "text" // Explicit type for serialization
}

@Serializable
@SerialName("image_url")
data class ImageContentPartRequest(
    @SerialName("image_url") val imageUrl: ImageUrlRequest
) : ContentPartRequest() {
    @Required
    @SerialName("type")
    override val type: String = "image_url"
}

@Serializable
@SerialName("input_audio")
data class AudioContentPartRequest(
    @SerialName("input_audio") val inputAudio: InputAudioRequest
) : ContentPartRequest() {
    @Required
    @SerialName("type")
    override val type: String = "input_audio"
}

@Serializable
@SerialName("file")
data class FileContentPartRequest(
    @SerialName("file") val file: FileDataRequest
) : ContentPartRequest() {
    @Required
    @SerialName("type")
    override val type: String = "file"
}

@Serializable
@SerialName("refusal")
data class RefusalContentPartRequest(
    @SerialName("refusal") val refusal: String
) : ContentPartRequest() {
    @Required
    @SerialName("type")
    override val type: String = "refusal"
} 