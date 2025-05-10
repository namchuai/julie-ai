package ai.julie.feature.modelconfig.domain

import ai.julie.core.model.ModelContextParams

interface RecreateModelContextUseCase {
    suspend fun recreateModelContext(modelContextParams: ModelContextParams)
}