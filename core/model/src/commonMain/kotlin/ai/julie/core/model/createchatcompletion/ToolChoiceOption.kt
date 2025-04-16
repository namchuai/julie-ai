package ai.julie.core.model.createchatcompletion

/**
 * Represents the options for controlling which tool is called by the model.
 */
sealed class ToolChoiceOption {
    /** Indicates the model will not call any tool and instead generates a message. */
    object None : ToolChoiceOption()

    /** Indicates the model can pick between generating a message or calling one or more tools. */
    object Auto : ToolChoiceOption()

    /** Indicates the model must call one or more tools. */
    object Required : ToolChoiceOption()

    /**
     * Forces the model to call a specific tool (currently only function is supported).
     * @param type The type of the tool (currently, only "function").
     * @param function Details of the function to call.
     */
    data class SpecificTool(
        val type: String = "function", // Currently only "function" is supported
        val function: FunctionReference
    ) : ToolChoiceOption()

    /**
     * Represents the reference to a specific function to be called.
     * @param name The name of the function.
     */
    data class FunctionReference(val name: String)
} 