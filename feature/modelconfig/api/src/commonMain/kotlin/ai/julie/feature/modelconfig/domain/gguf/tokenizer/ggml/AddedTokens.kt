package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class AddedTokens(
    override val value: List<String>,
) : GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.ggml.added_tokens"
    }
    
    /**
     * If present, tokens that were added after training.
     */
}