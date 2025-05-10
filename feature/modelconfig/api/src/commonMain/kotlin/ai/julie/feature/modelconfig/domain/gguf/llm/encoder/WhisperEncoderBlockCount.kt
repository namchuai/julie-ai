package ai.julie.feature.modelconfig.domain.gguf.llm.encoder

data class WhisperEncoderBlockCount(
    val value: ULong,
) {
    val key: String = "whisper.encoder.block_count"
    
    /**
     * Encoder block count (used by Whisper models).
     */
}