package ai.julie.feature.modelconfig.domain

import ai.julie.core.model.ModelLoadParams
import kotlinx.coroutines.flow.Flow

fun interface FlowOfModelLoadParam {
    fun flowOfModelLoadParam(modelId: String): Flow<ModelLoadParams>
}