package ai.julie.feature.modelconfig.domain.gguf.llm.scaling

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class OriginalContextLength(
    val architecture: SupportedArchitecture,
    val value: UInt,
) {
    val key: String = "${architecture.value}.rope.scaling.original_context_length"
    
    /**
     * The original context length of the base model.
     */
}