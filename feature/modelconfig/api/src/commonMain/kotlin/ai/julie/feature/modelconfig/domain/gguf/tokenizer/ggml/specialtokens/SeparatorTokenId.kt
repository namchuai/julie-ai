package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class SeparatorTokenId(
    override val value: UInt,
) : GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.ggml.separator_token_id"
    }
    
    /**
     * Separator token
     */
}

data class SeparatorToken(
    val value: String,
) {
    val key: String = KEY

    companion object {
        const val KEY = "tokenizer.ggml.separator_token"
    }
}
