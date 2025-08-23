package ai.julie.feature.modelconfig.domain

import ai.julie.feature.modelconfig.domain.gguf.GgufMetadata

fun interface StoreModelMetadata {
    suspend fun storeModelMetadata(modelId: String, metadata: GgufMetadata)
}