package ai.julie.core.model.createchatcompletion

/**
 * Represents a tool the model may call. Currently, only functions are supported.
 */
data class Tool(
    /**
     * Required.
     * The type of the tool. Currently, only "function" is supported.
     */
    val type: String = "function",

    /**
     * Required.
     * Details of the function tool.
     */
    val function: ToolFunction
)

/**
 * Describes the function details within a Tool.
 */
data class ToolFunction(
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
     * The parameters the function accepts, described as a JSON Schema object.
     * Omitting parameters defines a function with an empty parameter list.
     */
    val parameters: Map<String, Any>? = null, // Using Map for JSON Schema object

    /**
     * Optional.
     * Defaults to false.
     * Whether to enable strict schema adherence when generating the function call.
     * If set to true, the model will follow the exact schema defined in the parameters field.
     * Only a subset of JSON Schema is supported when strict is true.
     */
    val strict: Boolean? = false
) 