package ai.julie.whisperbinding

interface WhisperBinding {
    /**
     * Initialize a whisper context from a model file
     * @param modelPath Path to the whisper model file
     * @return WhisperContext if successful, null otherwise
     */
    fun initContext(modelPath: String): WhisperContext?

    /**
     * Transcribe audio data
     * @param context The whisper context
     * @param audioData Audio samples as float array (16kHz mono)
     * @return Transcribed text
     */
    fun transcribe(context: WhisperContext, audioData: FloatArray): String

    /**
     * Free the whisper context and release resources
     * @param context The whisper context to free
     */
    fun freeContext(context: WhisperContext)

    /**
     * Get whisper version
     * @return Version string
     */
    fun getVersion(): String
}

/**
 * Represents a whisper context handle
 */
expect class WhisperContext

/**
 * Factory function to create platform-specific WhisperBinding instance
 */
expect fun createWhisperBinding(): WhisperBinding