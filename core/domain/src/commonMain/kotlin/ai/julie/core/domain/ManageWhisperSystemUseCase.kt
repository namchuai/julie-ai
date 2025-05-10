package ai.julie.core.domain

import ai.julie.core.data.whisper.WhisperRepository
import ai.julie.logging.Logger

class ManageWhisperSystemUseCase(
    private val whisperRepository: WhisperRepository
) {
    /**
     * Get the current status of the whisper system
     * @return WhisperSystemStatus indicating current state
     */
    fun getStatus(): WhisperSystemStatus {
        return when {
            !whisperRepository.isInitialized() -> WhisperSystemStatus.NotInitialized
            !whisperRepository.isModelLoaded() -> WhisperSystemStatus.InitializedButNoModel
            else -> WhisperSystemStatus.Ready
        }
    }

    /**
     * Get the whisper version
     * @return Version string
     */
    fun getVersion(): String {
        return whisperRepository.getVersion()
    }

    /**
     * Clean up whisper resources
     */
    suspend fun cleanUp() {
        try {
            whisperRepository.cleanUp()
            Logger.d("Whisper system cleaned up")
        } catch (e: Exception) {
//            Logger.e("Error during whisper cleanup", e)
        }
    }

    /**
     * Check if whisper system is ready for use
     * @return true if ready, false otherwise
     */
    fun isReady(): Boolean {
        return getStatus() == WhisperSystemStatus.Ready
    }
}

/**
 * Represents the current status of the whisper system
 */
sealed class WhisperSystemStatus {
    object NotInitialized : WhisperSystemStatus()
    object InitializedButNoModel : WhisperSystemStatus()
    object Ready : WhisperSystemStatus()

    fun isReady(): Boolean = this is Ready
}