package ai.julie.feature.modelconfig.domain.gguf.llm.decoder

data class WhisperDecoderAttentionHeadCount(
    val value: ULong,
) {
    val key: String = "whisper.decoder.attention.head_count"
    
    /**
     * Decoder attention head count (used by Whisper models).
     */
}