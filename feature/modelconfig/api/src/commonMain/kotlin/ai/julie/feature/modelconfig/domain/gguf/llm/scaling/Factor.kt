package ai.julie.feature.modelconfig.domain.gguf.llm.scaling

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class Factor(
    val architecture: SupportedArchitecture,
    val value: Float,
) {
    val key: String = "${architecture.value}.rope.scaling.factor"
    
    /**
     * A scale factor for RoPE to adjust the context length.
     */
}