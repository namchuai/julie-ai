package ai.julie.core.data.llama

import ai.julie.core.model.ModelContextParams
import ai.julie.core.model.ModelLoadParams
import ai.julie.llamabinding.LlamaBinding

class LlamaRepositoryImpl : LlamaRepository {
    var llamaBinding: LlamaBinding? = null

    @Throws(IllegalStateException::class)
    override suspend fun initialize() {
        check(llamaBinding == null) { "LlamaBinding is already initialized" }

        llamaBinding = LlamaBinding()
        llamaBinding?.initialize()
    }

    override suspend fun loadModel(
        modelPath: String,
        modelLoadParams: ModelLoadParams,
        modelContextParams: ModelContextParams,
    ) {
        checkNotNull(llamaBinding) { "LlamaBinding is not initialized" }

        llamaBinding?.loadModel(
            modelPath = modelPath,
            modelLoadParams = modelLoadParams,
            modelContextParams = modelContextParams,
        )
    }

    override fun prompt(input: String) {
        checkNotNull(llamaBinding) { "LlamaBinding is not initialized" }

        val output = llamaBinding?.predict(input)
        println("Output: $output")
    }

    @Throws(IllegalStateException::class)
    override suspend fun cleanUp() {
        checkNotNull(llamaBinding) { "LlamaBinding is not initialized" }

        llamaBinding?.close()
        llamaBinding = null
    }
}