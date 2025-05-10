package ai.julie.feature.modelconfig.domain.gguf.llm.rope

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class Scale(
    val architecture: SupportedArchitecture,
    val value: Float,
) {
    val key: String = "${architecture.value}.rope.scale"
    
    /**
     * A scale factor for RoPE (used by LLaMA models).
     */
}