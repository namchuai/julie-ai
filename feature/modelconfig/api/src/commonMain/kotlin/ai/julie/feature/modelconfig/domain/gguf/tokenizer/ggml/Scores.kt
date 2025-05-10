package ai.julie.feature.modelconfig.domain.gguf.tokenizer.ggml

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class Scores(
    override val value: List<Float>,
) : GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.ggml.scores"
    }
    
    /**
     * If present, the score/probability of each token. If not present, all tokens 
     * are assumed to have equal probability. If present, it must have the same 
     * length and index as `tokens`.
     */
}