package ai.julie.feature.modelconfig.domain.gguf.llm.scaling

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class FineTuned(
    val architecture: SupportedArchitecture,
    val value: Boolean,
) {
    val key: String = "${architecture.value}.rope.scaling.finetuned"
    
    /**
     * True if model has been finetuned with RoPE scaling.
     */
}