package ai.julie.feature.modelconfig.domain.gguf.llm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class BlockCount(
    val architecture: SupportedArchitecture,
    val value: ULong,
) {
    val key: String = "${architecture.value}.block_count"
    
    /**
     * The number of blocks of attention+feed-forward layers (i.e. the bulk of the LLM). 
     * Does not include the input or embedding layers.
     */
}