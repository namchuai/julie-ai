package ai.julie.feature.modelconfig.domain.gguf.llm.rope

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class FreqBase(
    val architecture: SupportedArchitecture,
    val value: Float,
) {
    val key: String = "${architecture.value}.rope.freq_base"
    
    /**
     * The base frequency for RoPE.
     */
}