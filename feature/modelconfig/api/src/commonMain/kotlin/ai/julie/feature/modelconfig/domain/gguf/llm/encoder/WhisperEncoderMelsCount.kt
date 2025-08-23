package ai.julie.feature.modelconfig.domain.gguf.llm.encoder

data class WhisperEncoderMelsCount(
    val value: ULong,
) {
    val key: String = "whisper.encoder.mels_count"
    
    /**
     * Encoder mels count (Whisper-specific).
     */
}