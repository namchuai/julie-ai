package ai.julie.feature.modelconfig.domain.gguf.llm.attention

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class AlibiBiasMax(
    val architecture: SupportedArchitecture,
    val value: Float,
) {
    val key: String = "${architecture.value}.attention.alibi_bias_max"
    
    /**
     * The maximum bias to use for ALiBI (MPT-specific key name).
     */
}