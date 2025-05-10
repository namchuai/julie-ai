package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class Tokens(
    override val value: List<String>,
) : GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.ggml.tokens"
    }
    
    /**
     * A list of tokens indexed by the token ID used by the model.
     */
}