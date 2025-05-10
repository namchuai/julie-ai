package ai.julie.feature.modelconfig.domain.gguf.llm.decoder

data class WhisperDecoderContextLength(
    val value: ULong,
) {
    val key: String = "whisper.decoder.context_length"
    
    /**
     * Decoder context length (used by Whisper models).
     */
}