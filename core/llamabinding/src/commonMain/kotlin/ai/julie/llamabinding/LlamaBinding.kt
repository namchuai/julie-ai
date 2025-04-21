package ai.julie.llamabinding

import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Represents a loaded Llama model and its associated context.
 * Use LlamaPlatform.initializeBackend() before creating instances.
 * Call close() when finished to release native resources.
 *
 * Parameters for model/context loading are handled internally by the actual implementation.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class LlamaBinding(
    modelPath: String // Only model path is needed from common code
    // Removed LlamaModelParams and LlamaContextParams
) {
    /**
     * Releases the native resources associated with this model and context.
     * Must be called when the binding is no longer needed.
     */
    fun close()

    /**
     * Performs inference based on the provided prompt.
     * Note: This is a placeholder. The actual implementation requires
     * tokenization, sampling, and decoding logic using llama.cpp functions.
     * The corresponding JNI methods need to be added to NativeMethods.
     *
     * @param prompt The input text prompt.
     * @return The generated text response (placeholder).
     */
    fun predict(prompt: String): String // Placeholder

    /**
     * Gets the context size (n_ctx) for this binding's context.
     */
    fun getContextSize(): Int

    /**
     * Gets the vocabulary size (n_vocab) of the loaded model.
     */
    fun getVocabSize(): Int

    /**
     * Gets the embedding size (n_embd) of the loaded model.
     */
    fun getEmbeddingSize(): Int

    /**
     * Gets a description string for the loaded model.
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun getModelDescription(): String
}