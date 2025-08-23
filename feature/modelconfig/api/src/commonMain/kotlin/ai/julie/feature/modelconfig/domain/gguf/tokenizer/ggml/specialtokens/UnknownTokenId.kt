package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class UnknownTokenId(
    override val value: UInt,
) : GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.ggml.unknown_token_id"
    }
}

data class UnknownToken(
    val value: String,
) {
    val key: String = KEY

    companion object {
        const val KEY = "tokenizer.ggml.unknown_token"
    }
}
