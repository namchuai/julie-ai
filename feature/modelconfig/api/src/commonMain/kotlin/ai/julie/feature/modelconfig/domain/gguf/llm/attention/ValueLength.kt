package ai.julie.feature.modelconfig.domain.gguf.llm.attention

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class ValueLength(
    val architecture: SupportedArchitecture,
    val value: UInt,
) {
    val key: String = "${architecture.value}.attention.value_length"
    
    /**
     * The optional size of a value head, $d_v$. 
     * If not specified, it will be `n_embd / n_head`.
     */
}