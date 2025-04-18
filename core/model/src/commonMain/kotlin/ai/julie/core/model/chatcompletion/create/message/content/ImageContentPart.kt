package ai.julie.core.model.chatcompletion.create.message.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an image content part.
 */
@Serializable
@SerialName("image_url")
data class ImageContentPart(
    @SerialName("image_url") val imageUrl: ImageUrl,
    override val type: String = "image_url"
) : ContentPart {
    /**
     * Contains the URL and detail level for an image.
     */
    @Serializable
    data class ImageUrl(
        val url: String,
        /** Defaults to "auto". Specifies the detail level of the image. */
        val detail: String = "auto"
    )
} 