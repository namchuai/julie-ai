package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class PaddingTokenId(
    override val value: UInt,
) : GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.ggml.padding_token_id"
    }
}

data class PaddingToken(
    val value: String,
) {
    val key: String = KEY

    companion object {
        const val KEY = "tokenizer.ggml.padding_token"
    }
}
