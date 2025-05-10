package ai.julie.feature.modelconfig.domain.gguf.llm.attention

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class KeyLength(
    val architecture: SupportedArchitecture,
    val value: UInt,
) {
    val key: String = "${architecture.value}.attention.key_length"
    
    /**
     * The optional size of a key head, $d_k$. 
     * If not specified, it will be `n_embd / n_head`.
     */
}