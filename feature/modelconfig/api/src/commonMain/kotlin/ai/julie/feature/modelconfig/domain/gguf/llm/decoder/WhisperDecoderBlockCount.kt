package ai.julie.feature.modelconfig.domain.gguf.llm.decoder

data class WhisperDecoderBlockCount(
    val value: ULong,
) {
    val key: String = "whisper.decoder.block_count"
    
    /**
     * Decoder block count (used by Whisper models).
     */
}