package ai.julie.feature.modelconfig.domain

import ai.julie.core.model.ModelContextParams
import kotlinx.coroutines.flow.Flow

fun interface FlowOfModelContextParam {

    fun flowOfModelContextParam(
        modelId: String
    ): Flow<ModelContextParams>
}
