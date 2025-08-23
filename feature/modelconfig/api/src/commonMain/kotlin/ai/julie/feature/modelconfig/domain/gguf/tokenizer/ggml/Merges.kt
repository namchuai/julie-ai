package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class Merges(
    override val value: List<String>,
) : GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.ggml.merges"
    }
    
    /**
     * If present, the merges of the tokenizer. If not present, the tokens 
     * are assumed to be atomic.
     */
}