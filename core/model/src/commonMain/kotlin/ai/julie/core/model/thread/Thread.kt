package ai.julie.core.model.thread

/**
 * Represents a thread that contains messages.
 */
data class Thread(
    /**
     * The identifier, which can be referenced in API endpoints.
     */
    val id: String,

    /**
     * The Unix timestamp (in seconds) for when the thread was created.
     */
    val createdAt: Long,

    /**
     * Set of key-value pairs that can be attached to an object for additional information.
     * Keys are strings with a maximum length of 64 characters.
     * Values are strings with a maximum length of 512 characters.
     */
    val metadata: Map<String, String>? = null,

    /**
     * A set of resources that are made available to the assistant's tools in this thread.
     * The resources are specific to the type of tool.
     */
    val toolResources: ThreadToolResources? = null
)