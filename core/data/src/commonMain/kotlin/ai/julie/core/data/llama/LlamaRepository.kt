package ai.julie.core.data.llama

import ai.julie.core.model.LlamaSamplerSettings
import ai.julie.core.model.ModelContextParams
import ai.julie.core.model.ModelLoadParams
import ai.julie.llamabinding.LlamaBinding
import ai.julie.llamabinding.LlamaProgressCallback
import kotlinx.coroutines.flow.Flow

class LlamaRepository : Promptable, InitializeLlamaBackend, LoadModel, CleanUp {
    var llamaBinding: LlamaBinding? = null

    override suspend fun initialize() {
        if (llamaBinding == null) {
            llamaBinding = LlamaBinding()
            llamaBinding?.initialize()
        }
    }

    override suspend fun loadModel(
        modelPath: String,
        modelLoadParams: ModelLoadParams,
        modelContextParams: ModelContextParams,
        progressCallback: LlamaProgressCallback?
    ) {
        checkNotNull(llamaBinding) { LLAMA_NOT_INITIALIZED_ERROR }

        llamaBinding?.loadModel(
            modelPath = modelPath,
            modelLoadParams = modelLoadParams,
            modelContextParams = modelContextParams,
            progressCallback = progressCallback
        )
    }

    override fun prompt(input: String, samplerSettings: LlamaSamplerSettings): Flow<String> {
        return llamaBinding?.predict(input, samplerSettings) ?: throw IllegalStateException(LLAMA_NOT_INITIALIZED_ERROR)
    }

    override suspend fun cleanUp() {
        checkNotNull(llamaBinding) { LLAMA_NOT_INITIALIZED_ERROR }

        llamaBinding?.close()
        llamaBinding = null
    }

    suspend fun recreateContext(newContextParams: ModelContextParams) {
       checkNotNull(llamaBinding) { LLAMA_NOT_INITIALIZED_ERROR }
        llamaBinding?.recreateContext(newContextParams)
    }

    companion object {
        private const val LLAMA_NOT_INITIALIZED_ERROR = "LlamaBinding is not initialized."
    }
}