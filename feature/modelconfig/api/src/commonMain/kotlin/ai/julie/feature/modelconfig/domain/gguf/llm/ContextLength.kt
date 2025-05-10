package ai.julie.feature.modelconfig.domain.gguf.llm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class ContextLength(
    val architecture: SupportedArchitecture,
    val value: ULong,
) {
    val key: String = "${architecture.value}.context_length"
    
    /**
     * Also known as `n_ctx`. length of the context (in tokens) that the model was trained on. 
     * For most architectures, this is the hard limit on the length of the input. 
     * Architectures, like RWKV, that are not reliant on transformer-style attention may be 
     * able to handle larger inputs, but this is not guaranteed.
     */
}