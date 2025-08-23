package ai.julie.feature.modelconfig.domain.gguf.llm.attention

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class MaxAlibiBias(
    val architecture: SupportedArchitecture,
    val value: Float,
) {
    val key: String = "${architecture.value}.attention.max_alibi_bias"
    
    /**
     * The maximum bias to use for ALiBI.
     */
}