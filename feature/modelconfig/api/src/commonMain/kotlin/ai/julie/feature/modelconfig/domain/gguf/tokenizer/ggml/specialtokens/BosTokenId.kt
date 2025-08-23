package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml.specialtokens

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class BosTokenId(
    override val value: UInt,
) : GgufTokenizer {
    override val key: String = KEY

    companion object {
        const val KEY = "tokenizer.ggml.bos_token_id"
    }

    /**
     * Beginning of sequence marker
     */
}

data class BosToken(
    val value: String,
) {
    val key: String = KEY

    companion object {
        const val KEY = "tokenizer.ggml.bos_token"
    }
}
