package ai.julie.core.domain

import ai.julie.core.data.whisper.WhisperRepository
import ai.julie.logging.Logger

class InitWhisperModelUseCase(
    private val whisperRepository: WhisperRepository
) {
    /**
     * Initialize the whisper system and load a model
     * @param modelPath Path to the whisper model file
     * @return Result indicating success or failure
     */
    suspend fun execute(modelPath: String): Result<Unit> {
        return try {
            // Initialize whisper system if not already initialized
            if (!whisperRepository.isInitialized()) {
                whisperRepository.initialize()
                Logger.d("Whisper system initialized")
            }

            // Load the model
            val result = whisperRepository.loadModel(modelPath)
            if (result.isSuccess) {
                Logger.d("Whisper model loaded successfully")
                Result.success(Unit)
            } else {
                Logger.e("Failed to load whisper model: ${result.exceptionOrNull()?.message}")
                result
            }
        } catch (e: Exception) {
//            Logger.e("Exception during whisper initialization", e)
            Result.failure(e)
        }
    }

    /**
     * Check if whisper is ready for transcription
     * @return true if initialized and model is loaded
     */
    fun isReady(): Boolean {
        return whisperRepository.isInitialized() && whisperRepository.isModelLoaded()
    }
}