package ai.julie.feature.modelconfig.domain.gguf.llm.encoder

data class WhisperEncoderAttentionHeadCount(
    val value: ULong,
) {
    val key: String = "whisper.encoder.attention.head_count"
    
    /**
     * Encoder attention head count (used by Whisper models).
     */
}