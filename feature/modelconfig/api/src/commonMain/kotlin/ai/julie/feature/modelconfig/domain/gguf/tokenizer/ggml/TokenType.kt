package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

enum class GgmlTokenType(val value: UInt) {
    NORMAL(1u),
    UNKNOWN(2u),
    CONTROL(3u),
    USER_DEFINED(4u),
    UNUSED(5u),
    BYTE(6u),
}

data class TokenType(
    override val value: List<GgmlTokenType>,
) : GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.ggml.token_type"
    }

    /**
     * The token type (1=normal, 2=unknown, 3=control, 4=user defined, 5=unused, 6=byte).
     * If present, it must have the same length and index as `tokens`.
     */
}