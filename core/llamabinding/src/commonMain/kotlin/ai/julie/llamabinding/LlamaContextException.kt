package ai.julie.llamabinding

/**
 * Custom exception for llama.cpp context-related errors
 */
sealed class LlamaContextException(message: String, cause: Throwable? = null) :
    Exception(message, cause) {

    /**
     * Thrown when the prompt exceeds the available context window
     */
    class ContextTooSmallException(
        val promptTokens: Int,
        val maxContextTokens: Int,
        val promptLength: Int
    ) : LlamaContextException(
        "Prompt too long: $promptTokens tokens exceeds context size of $maxContextTokens tokens " +
                "(prompt length: $promptLength characters). " +
                "Consider increasing context size or reducing prompt length."
    )

    /**
     * Thrown when tokenization fails due to invalid content
     */
    class TokenizationFailedException(
        val errorCode: Int,
        val promptLength: Int
    ) : LlamaContextException(
        "Failed to tokenize prompt (error code: $errorCode). " +
                "Prompt length: $promptLength characters. " +
                "This may be due to invalid characters or prompt being too long."
    )

    /**
     * Thrown when model context is not properly initialized
     */
    class ContextNotInitializedException : LlamaContextException(
        "Llama context is not initialized or has been closed. " +
                "Please ensure the model is loaded before attempting inference."
    )

    /**
     * Thrown when model is not loaded
     */
    class ModelNotLoadedException : LlamaContextException(
        "Llama model is not initialized or has been closed. " +
                "Please load a model before attempting inference."
    )
}