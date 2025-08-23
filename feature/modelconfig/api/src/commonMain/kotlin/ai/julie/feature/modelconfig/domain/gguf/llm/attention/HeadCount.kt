package ai.julie.feature.modelconfig.domain.gguf.llm.attention

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class HeadCount(
    val architecture: SupportedArchitecture,
    val value: ULong,
) {
    val key: String = "${architecture.value}.attention.head_count"
    
    /**
     * Also known as `n_head`. Number of attention heads.
     */
}