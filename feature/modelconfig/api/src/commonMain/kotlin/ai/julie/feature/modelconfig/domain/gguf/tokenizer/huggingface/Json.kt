package ai.julie.feature.modelconfig.domain.gguf.tokenizer.huggingface

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class Json(
    override val value: String,
) : GgufTokenizer {
    override val key: String = KEY

    companion object {
        const val KEY = "tokenizer.huggingface.json"
    }
}