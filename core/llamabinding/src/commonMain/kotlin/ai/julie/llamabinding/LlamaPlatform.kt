package ai.julie.llamabinding

/**
 * Expected object for handling platform-specific Llama backend initialization and cleanup.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object LlamaPlatform {
    /**
     * Initializes the Llama backend.
     * Should be called once before using any LlamaBinding instances.
     * Idempotent: subsequent calls have no effect.
     */
    fun initializeBackend()

    /**
     * Frees the Llama backend resources.
     * Should be called once when the application is shutting down and no LlamaBindings are active.
     * Idempotent: subsequent calls have no effect.
     */
    fun freeBackend()
} 