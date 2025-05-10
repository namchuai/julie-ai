package ai.julie.feature.modelconfig.domain

import ai.julie.core.data.llama.LlamaRepository
import ai.julie.core.model.ModelContextParams

class RecreateModelContextUseCaseImpl(
    private val llamaRepository: LlamaRepository
) : RecreateModelContextUseCase {
    
    override suspend fun recreateModelContext(modelContextParams: ModelContextParams) {
        llamaRepository.recreateContext(modelContextParams)
    }
}