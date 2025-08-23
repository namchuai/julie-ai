package ai.julie.feature.modelconfig.domain.gguf.llm.attention

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class UseNorm(
    val architecture: SupportedArchitecture,
    val value: Boolean,
) {
    val key: String = "${architecture.value}.attention.use_norm"
    
    /**
     * Whether to use normalization in attention (Falcon-specific).
     */
}