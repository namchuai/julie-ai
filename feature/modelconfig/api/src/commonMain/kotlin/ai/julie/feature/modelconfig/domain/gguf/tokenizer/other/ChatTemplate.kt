package ai.julie.feature.modelconfig.domain.gguf.tokenizer.other

import ai.julie.feature.modelconfig.domain.gguf.tokenizer.GgufTokenizer

data class ChatTemplate(
    override val value: String,
): GgufTokenizer {
    override val key: String = KEY
    
    companion object {
        const val KEY = "tokenizer.chat_template"
    }
    
    /**
     * A Jinja template that specifies the input format expected by the model. 
     * For more details see: https://huggingface.co/docs/transformers/main/en/chat_templating
     */
}