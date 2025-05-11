package ai.julie.core.domain

import ai.julie.core.data.llama.LlamaRepository
import ai.julie.core.model.ModelContextParams
import ai.julie.core.model.ModelLoadParams
import ai.julie.core.model.aimodel.LocalAiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InitLocalModelUseCase(
    private val llamaRepository: LlamaRepository,
) {
    suspend operator fun invoke(model: LocalAiModel) {
        val path = model.localPath
        checkNotNull(path) { "Model path is null" }

        withContext(Dispatchers.Default) {
            llamaRepository.initialize()
            llamaRepository.loadModel(
                modelPath = path,
                modelLoadParams = ModelLoadParams(),
                modelContextParams = ModelContextParams(),
            )
        }
    }
}