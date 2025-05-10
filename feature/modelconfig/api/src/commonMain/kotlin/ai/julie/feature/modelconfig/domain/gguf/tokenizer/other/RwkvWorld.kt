package ai.julie.feature.modelconfig.domain.gguf.tokenizer.other

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class RwkvWorld(
    override val value: String,
) : GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.rwkv.world"
    }

    /**
     * A RWKV World tokenizer, like https://github.com/BlinkDL/ChatRWKV/blob/main/tokenizer/rwkv_vocab_v20230424.txt.
     * This text file should be included verbatim.
     */
}