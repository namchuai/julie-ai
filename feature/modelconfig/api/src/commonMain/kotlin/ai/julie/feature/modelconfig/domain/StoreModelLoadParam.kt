package ai.julie.feature.modelconfig.domain

import ai.julie.core.model.ModelLoadParams

fun interface StoreModelLoadParam {
    suspend fun storeModelLoadParam(modelId: String, modelLoadParams: ModelLoadParams)
}