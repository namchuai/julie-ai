package ai.julie.feature.modelconfig.domain.gguf.llm.decoder

data class WhisperDecoderEmbeddingLength(
    val value: ULong,
) {
    val key: String = "whisper.decoder.embedding_length"
    
    /**
     * Decoder embedding length (used by Whisper models).
     */
}