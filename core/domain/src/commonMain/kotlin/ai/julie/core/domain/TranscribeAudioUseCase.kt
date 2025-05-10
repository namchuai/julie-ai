package ai.julie.core.domain

import ai.julie.core.data.whisper.WhisperRepository
import ai.julie.logging.Logger

class TranscribeAudioUseCase(
    private val whisperRepository: WhisperRepository
) {
    /**
     * Transcribe audio data to text
     * @param audioData Float array containing audio samples (16kHz mono)
     * @return Result containing transcribed text or error
     */
    suspend fun execute(audioData: FloatArray): Result<String> {
        return try {
            // Validate input
            if (audioData.isEmpty()) {
                Logger.w("Empty audio data provided")
                return Result.failure(IllegalArgumentException("Audio data is empty"))
            }

            // Check if whisper is ready
            if (!whisperRepository.isInitialized()) {
                Logger.e("Whisper is not initialized")
                return Result.failure(IllegalStateException("Whisper is not initialized"))
            }

            if (!whisperRepository.isModelLoaded()) {
                Logger.e("Whisper model is not loaded")
                return Result.failure(IllegalStateException("Whisper model is not loaded"))
            }

            // Debug audio data
            Logger.d("Starting transcription for ${audioData.size} audio samples")

            // Check if audio data is valid
            val nonZeroSamples = audioData.count { it != 0.0f }
            val maxAmplitude = audioData.maxOrNull() ?: 0.0f
            val minAmplitude = audioData.minOrNull() ?: 0.0f
            val avgAmplitude = audioData.map { kotlin.math.abs(it) }.average()

            Logger.d("Audio stats: nonZero=$nonZeroSamples, max=$maxAmplitude, min=$minAmplitude, avgAbs=$avgAmplitude")

            if (nonZeroSamples == 0) {
                Logger.w("Audio data contains only zeros - no sound detected")
                return Result.failure(IllegalStateException("No sound detected in audio"))
            }

            // Perform transcription
            val result = whisperRepository.transcribe(audioData)

            if (result.isSuccess) {
                val text = result.getOrNull()?.trim() ?: ""
                Logger.d("Transcription completed: ${text.take(100)}...")
                if (text.isEmpty()) {
                    Logger.w("Transcription resulted in empty text")
                    Result.failure(IllegalStateException("No speech detected"))
                } else {
                    Result.success(text)
                }
            } else {
                Logger.e("Transcription failed: ${result.exceptionOrNull()?.message}")
                result
            }
        } catch (e: Exception) {
//            Logger.e("Exception during transcription", e)
            Result.failure(e)
        }
    }

    /**
     * Check if the whisper system is ready for transcription
     * @return true if ready, false otherwise
     */
    fun isReady(): Boolean {
        return whisperRepository.isInitialized() && whisperRepository.isModelLoaded()
    }
}