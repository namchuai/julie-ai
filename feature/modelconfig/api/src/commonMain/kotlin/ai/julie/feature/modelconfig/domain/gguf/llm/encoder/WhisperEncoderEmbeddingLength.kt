package ai.julie.feature.modelconfig.domain.gguf.llm.encoder

data class WhisperEncoderEmbeddingLength(
    val value: ULong,
) {
    val key: String = "whisper.encoder.embedding_length"
    
    /**
     * Encoder embedding length (used by Whisper models).
     */
}