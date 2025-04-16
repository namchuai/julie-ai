package ai.julie.core.model.createchatcompletion

/**
 * Represents the options for controlling which function is called by the model,
 * used by the deprecated `functionCall` field.
 */
sealed class FunctionCallOption {
    /** Indicates the model will not call a function and instead generates a message. */
    object None : FunctionCallOption()

    /** Indicates the model can pick between generating a message or calling a function. */
    object Auto : FunctionCallOption()

    /**
     * Forces the model to call the specified function.
     * @param name The name of the function to call.
     */
    data class Specific(val name: String) : FunctionCallOption()
} 