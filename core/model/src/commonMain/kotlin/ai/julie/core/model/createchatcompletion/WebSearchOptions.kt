package ai.julie.core.model.createchatcompletion

/**
 * Approximate location parameters for the web search.
 */
data class ApproximateLocation(
    /**
     * Optional.
     * Free text input for the city of the user, e.g. San Francisco.
     */
    val city: String? = null,

    /**
     * Optional.
     * The two-letter ISO country code of the user, e.g. US.
     */
    val country: String? = null,

    /**
     * Optional.
     * Free text input for the region of the user, e.g. California.
     */
    val region: String? = null,

    /**
     * Optional.
     * The IANA timezone of the user, e.g. America/Los_Angeles.
     */
    val timezone: String? = null,

    /**
     * Required.
     * The type of location approximation. Always "approximate".
     */
    val type: String = "approximate"
)

/**
 * Contains the approximate location parameters for the search.
 */
data class UserLocation(
    /**
     * Required.
     * Approximate location parameters for the search.
     */
    val approximate: ApproximateLocation
)

/**
 * Configuration options for the web search tool.
 */
data class WebSearchOptions(
    /**
     * Optional.
     * Defaults to medium.
     * High level guidance for the amount of context window space to use for the search.
     * One of "low", "medium", or "high".
     */
    val searchContextSize: String? = "medium",

    /**
     * Optional.
     * Approximate location parameters for the search.
     */
    val userLocation: UserLocation? = null
) 