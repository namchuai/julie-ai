package ai.julie.llamabinding

import ai.julie.core.model.LlamaSamplerSettings
import ai.julie.core.model.ModelContextParams
import ai.julie.core.model.ModelLoadParams
import kotlinx.coroutines.flow.Flow
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Represents a loaded Llama model and its associated context.
 * Use LlamaPlatform.initializeBackend() before creating instances.
 * Call close() when finished to release native resources.
 *
 * Parameters for model/context loading are handled internally by the actual implementation.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class LlamaBinding() {
    suspend fun initialize()

    suspend fun loadModel(
        modelPath: String,
        modelLoadParams: ModelLoadParams,
        modelContextParams: ModelContextParams,
        progressCallback: LlamaProgressCallback?
    )

    /**
     * Releases the native resources associated with this model and context.
     * Must be called when the binding is no longer needed.
     */
    fun close()

    /**
     * Performs inference based on the provided prompt with custom sampler settings.
     *
     * @param prompt The input text prompt.
     * @param samplerSettings The sampling parameters (temperature, top_k, etc.)
     * @return The generated text response as a flow.
     */
    fun predict(prompt: String, samplerSettings: LlamaSamplerSettings): Flow<String>

    /**
     * Gets the context size (n_ctx) for this binding's context.
     */
    fun getContextSize(): Int

    /**
     * Gets the vocabulary size (n_vocab) of the loaded model.
     */
    fun getVocabSize(): Int

    /**
     * Recreates the context with new parameters while preserving the loaded model.
     * This allows changing context length, batch size, etc. without reloading the entire model.
     * TODO: NamH recheck this function. seems like it's not working yet
     * @param newContextParams The new context parameters to use
     */
    suspend fun recreateContext(newContextParams: ModelContextParams)

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