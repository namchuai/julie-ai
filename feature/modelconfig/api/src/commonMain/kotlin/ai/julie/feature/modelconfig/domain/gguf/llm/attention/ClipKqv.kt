package ai.julie.feature.modelconfig.domain.gguf.llm.attention

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class ClipKqv(
    val architecture: SupportedArchitecture,
    val value: Float,
) {
    val key: String = "${architecture.value}.attention.clip_kqv"
    
    /**
     * Value to clip the values of the Q, K, and V tensors (MPT-specific key name).
     */
}