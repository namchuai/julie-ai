package ai.julie.core.data.llama

import ai.julie.core.model.ModelContextParams
import ai.julie.core.model.ModelLoadParams
import ai.julie.llamabinding.LlamaProgressCallback

fun interface LoadModel {
    suspend fun loadModel(
        modelPath: String,
        modelLoadParams: ModelLoadParams,
        modelContextParams: ModelContextParams,
        progressCallback: LlamaProgressCallback?
    )
}