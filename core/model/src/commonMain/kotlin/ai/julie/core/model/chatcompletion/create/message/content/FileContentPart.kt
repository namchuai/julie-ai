package ai.julie.core.model.chatcompletion.create.message.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a file content part for text generation inputs.
 */
@Serializable
@SerialName("file")
data class FileContentPart(
    @SerialName("file") val fileData: FileData,
    // The API docs structure nests the file data but specifies the top-level type is always "file".
    override val type: String = "file"
) : ContentPart {
    /**
     * Contains file information, either as base64 data or a file ID.
     */
    @Serializable
    data class FileData(
        /** The base64 encoded file data. */
        @SerialName("file_data") val fileDataBase64: String? = null,
        /** The ID of an uploaded file. */
        @SerialName("file_id") val fileId: String? = null,
        /** The name of the file. */
        val filename: String? = null
    ) {
        init {
            require(fileDataBase64 != null || fileId != null) {
                "Either file_data (base64) or file_id must be provided for FileData."
            }
            // Optional: Add validation if filename is required when file_data is present, etc.
        }
    }
} 