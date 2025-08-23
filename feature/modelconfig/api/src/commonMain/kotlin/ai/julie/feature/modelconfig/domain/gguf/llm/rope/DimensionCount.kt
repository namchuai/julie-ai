package ai.julie.feature.modelconfig.domain.gguf.llm.rope

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class DimensionCount(
    val architecture: SupportedArchitecture,
    val value: ULong,
) {
    val key: String = "${architecture.value}.rope.dimension_count"
    
    /**
     * The number of rotary dimensions for RoPE.
     */
}