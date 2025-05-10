package ai.julie.feature.modelconfig.domain

import ai.julie.feature.modelconfig.domain.gguf.GgufMetadata
import kotlinx.coroutines.flow.Flow

fun interface FlowOfModelMetadata {
    fun flowOfModelMetadata(modelId: String): Flow<GgufMetadata>
}