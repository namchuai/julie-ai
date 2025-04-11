package ai.julie.core.model.message

/**
 * Represents a file attached to a message.
 */
data class Attachment(
    /**
     * The ID of the file to attach to the message.
     */
    val fileId: String,

    /**
     * The tools to add this file to.
     */
    val tools: List<Tool>
)
