package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

enum class GgmlTokenizerModel(val value: String) {
    LLAMA("llama"),
    REPLIT("replit"),
    GPT2("gpt2"),
    RWKV("rwkv")
}

data class Model(
    override val value: GgmlTokenizerModel,
) : GgufTokenizer {
    override val key: String = KEY

    companion object {
        const val KEY = "tokenizer.ggml.model"
    }

    /**
     * The name of the tokenizer model.
     * - `llama`: Llama style SentencePiece (tokens and scores extracted from HF `tokenizer.model`)
     * - `replit`: Replit style SentencePiece (tokens and scores extracted from HF `spiece.model`)
     * - `gpt2`: GPT-2 / GPT-NeoX style BPE (tokens extracted from HF `tokenizer.json`)
     * - `rwkv`: RWKV tokenizer
     */
}