package ai.julie.core.domain

import ai.julie.core.data.llama.LlamaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PromptLocalModelUseCase(
    private val llamaRepository: LlamaRepository,
) {
    suspend operator fun invoke(input: String) {
        withContext(Dispatchers.Default) {
            llamaRepository.prompt(input)
        }
    }
}