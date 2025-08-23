package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class EosTokenId(
    override val value: UInt,
) : GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.ggml.eos_token_id"
    }
}

data class EosToken(
    val value: String,
) {
    val key: String = KEY

    companion object {
        const val KEY = "tokenizer.ggml.eos_token"
    }
}
