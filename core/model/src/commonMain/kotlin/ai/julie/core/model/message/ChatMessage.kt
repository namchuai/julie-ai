package ai.julie.core.model.message

/**
 * Represents a message within a thread.
 */
data class ChatMessage(
    /**
     * The identifier, which can be referenced in API endpoints.
     */
    val id: String,

    /**
     * The entity that produced the message. One of `user` or `assistant`.
     */
    val role: String,

    /**
     * The content of the message in array of text and/or images.
     */
    val content: List<Content>,

    /**
     * The thread ID that this message belongs to.
     */
    val threadId: String,

    /**
     * The Unix timestamp (in seconds) for when the message was created.
     */
    val createdAt: Long,

    /**
     * The status of the message, which can be either `in_progress`, `incomplete`, or `completed`.
     */
    val status: String,

    /**
     * If applicable, the ID of the assistant that authored this message.
     */
    val assistantId: String? = null,

    /**
     * A list of files attached to the message, and the tools they were added to.
     */
    val attachments: List<Attachment>? = null,

    /**
     * The Unix timestamp (in seconds) for when the message was completed.
     */
    val completedAt: Long? = null,

    /**
     * The Unix timestamp (in seconds) for when the message was marked as incomplete.
     */
    val incompleteAt: Long? = null,

    /**
     * On an incomplete message, details about why the message is incomplete.
     */
    val incompleteDetails: IncompleteDetails? = null,

    /**
     * Set of key-value pairs that can be attached to an object for additional information.
     * Keys are strings with a maximum length of 64 characters.
     * Values are strings with a maximum length of 512 characters.
     */
    val metadata: Map<String, String>? = null,

    /**
     * The ID of the run associated with the creation of this message.
     * Value is `null` when messages are created manually using the create message or create thread endpoints.
     */
    val runId: String? = null
)
