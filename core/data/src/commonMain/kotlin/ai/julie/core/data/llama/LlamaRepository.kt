package ai.julie.core.data.llama

import ai.julie.core.model.ModelContextParams
import ai.julie.core.model.ModelLoadParams

interface LlamaRepository {
    suspend fun initialize()

    suspend fun loadModel(
        modelPath: String,
        modelLoadParams: ModelLoadParams,
        modelContextParams: ModelContextParams,
    )

    fun prompt(input: String)

    suspend fun cleanUp()
}