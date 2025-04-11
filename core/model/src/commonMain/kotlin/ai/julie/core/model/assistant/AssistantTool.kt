package ai.julie.core.model.assistant

/**
 * Represents a tool that can be used by an assistant.
 */
sealed class AssistantTool {
    /**
     * Code interpreter tool for the assistant.
     */
    data class CodeInterpreter(
        /**
         * The type of tool being defined: `code_interpreter`
         */
        val type: String = "code_interpreter"
    ) : AssistantTool()

    /**
     * File search tool for the assistant.
     */
    data class FileSearch(
        /**
         * The type of tool being defined: `file_search`
         */
        val type: String = "file_search",

        /**
         * Overrides for the file search tool.
         */
        val fileSearch: FileSearchOverrides? = null
    ) : AssistantTool()

    /**
     * Function tool for the assistant.
     */
    data class Function(
        /**
         * The type of tool being defined: `function`
         */
        val type: String = "function",

        /**
         * The function definition.
         */
        val function: FunctionDefinition
    ) : AssistantTool()
}

/**
 * Overrides for the file search tool.
 */
data class FileSearchOverrides(
    /**
     * The maximum number of results the file search tool should output.
     * The default is 20 for `gpt-4*` models and 5 for `gpt-3.5-turbo`.
     * This number should be between 1 and 50 inclusive.
     */
    val maxNumResults: Int? = null,

    /**
     * The ranking options for the file search.
     * If not specified, the file search tool will use the `auto` ranker and a score_threshold of 0.
     */
    val rankingOptions: RankingOptions? = null
)

/**
 * The ranking options for the file search.
 */
data class RankingOptions(
    /**
     * The score threshold for the file search.
     * All values must be a floating point number between 0 and 1.
     */
    val scoreThreshold: Double? = null,

    /**
     * The ranker to use for the file search.
     * If not specified will use the `auto` ranker.
     */
    val ranker: String? = null
)

/**
 * Defines a function that the assistant can call.
 */
data class FunctionDefinition(
    /**
     * The name of the function to be called.
     * Must be a-z, A-Z, 0-9, or contain underscores and dashes, with a maximum length of 64.
     */
    val name: String,

    /**
     * A description of what the function does, used by the model to choose when and how to call the function.
     */
    val description: String? = null,

    /**
     * The parameters the functions accepts, described as a JSON Schema object.
     * Omitting `parameters` defines a function with an empty parameter list.
     */
    val parameters: Map<String, Any>? = null,

    /**
     * Whether to enable strict schema adherence when generating the function call.
     * If set to true, the model will follow the exact schema defined in the `parameters` field.
     */
    val strict: Boolean? = null
)
