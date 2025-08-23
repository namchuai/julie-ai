package ai.julie.core.data.whisper

interface WhisperRepository {
    /**
     * Initialize the whisper binding system
     */
    suspend fun initialize()

    /**
     * Load a whisper model from the given path
     * @param modelPath Path to the whisper model file (.bin format)
     * @return Result indicating success or failure
     */
    suspend fun loadModel(modelPath: String): Result<Unit>

    /**
     * Transcribe audio data to text
     * @param audioData Float array containing audio samples (16kHz mono)
     * @return Result containing transcribed text or error
     */
    suspend fun transcribe(audioData: FloatArray): Result<String>

    /**
     * Check if the whisper system is initialized
     * @return true if initialized, false otherwise
     */
    fun isInitialized(): Boolean

    /**
     * Check if a model is loaded and ready for transcription
     * @return true if model is loaded, false otherwise
     */
    fun isModelLoaded(): Boolean

    /**
     * Get the version of the whisper.cpp library
     * @return Version string
     */
    fun getVersion(): String

    /**
     * Clean up resources and free the whisper context
     */
    suspend fun cleanUp()
}