package ai.julie.feature.modelconfig.domain.gguf.llm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class EmbeddingLength(
    val architecture: SupportedArchitecture,
    val value: ULong,
) {
    val key: String = "${architecture.value}.embedding_length"
    
    /**
     * Also known as `n_embd`. Embedding layer size.
     */
}