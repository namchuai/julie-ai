package ai.julie.feature.modelconfig.domain

import ai.julie.core.model.ModelContextParams

fun interface StoreModelContextParam {
    suspend fun storeModelContextParam(
        modelId: String,
        modelContextParams: ModelContextParams,
    )
}