package ai.julie.core.model.message

/**
 * Details about why a message is incomplete.
 */
data class IncompleteDetails(
    /**
     * The reason why the message is incomplete.
     */
    val reason: String
)