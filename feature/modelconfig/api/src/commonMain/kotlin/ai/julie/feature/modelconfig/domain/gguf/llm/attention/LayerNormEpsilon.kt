package ai.julie.feature.modelconfig.domain.gguf.llm.attention

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class LayerNormEpsilon(
    val architecture: SupportedArchitecture,
    val value: Float,
) {
    val key: String = "${architecture.value}.attention.layer_norm_epsilon"
    
    /**
     * Layer normalization epsilon.
     */
}