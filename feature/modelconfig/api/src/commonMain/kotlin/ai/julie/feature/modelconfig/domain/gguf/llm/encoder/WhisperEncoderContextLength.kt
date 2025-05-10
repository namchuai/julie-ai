package ai.julie.feature.modelconfig.domain.gguf.llm.encoder

data class WhisperEncoderContextLength(
    val value: ULong,
) {
    val key: String = "whisper.encoder.context_length"
    
    /**
     * Encoder context length (used by Whisper models).
     */
}