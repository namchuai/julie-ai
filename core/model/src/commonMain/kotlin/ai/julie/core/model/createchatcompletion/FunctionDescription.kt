package ai.julie.core.model.createchatcompletion

/**
 * Represents a function description for the deprecated `functions` field.
 *
 * Deprecated in favor of `tools`.
 */
@Deprecated("Deprecated in favor of tools.")
data class FunctionDescription(
    /**
     * Required.
     * The name of the function to be called. Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum length of 64.
     */
    val name: String,

    /**
     * Optional.
     * A description of what the function does, used by the model to choose when and how to call the function.
     */
    val description: String? = null,

    /**
     * Optional.
     * The parameters the functions accepts, described as a JSON Schema object.
     * Omitting parameters defines a function with an empty parameter list.
     * Represented as Any? here, expecting a map structure during serialization.
     */
    val parameters: Any? = null
) 