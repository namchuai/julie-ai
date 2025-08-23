package ai.julie.core.audio

import kotlinx.coroutines.flow.Flow

/**
 * Audio recording configuration
 */
data class AudioConfig(
    val sampleRate: Int = 16000,  // 16kHz for whisper
    val channels: Int = 1,        // Mono for whisper
    val bitsPerSample: Int = 16   // 16-bit PCM
)

/**
 * Audio recording state
 */
sealed class AudioRecordingState {
    object Idle : AudioRecordingState()
    object Recording : AudioRecordingState()
    object Stopped : AudioRecordingState()
    data class Error(val message: String, val cause: Throwable? = null) : AudioRecordingState()
}

/**
 * Audio recording result
 */
data class AudioRecordingResult(
    val audioData: FloatArray,
    val sampleRate: Int,
    val durationMs: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AudioRecordingResult

        if (!audioData.contentEquals(other.audioData)) return false
        if (sampleRate != other.sampleRate) return false
        if (durationMs != other.durationMs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = audioData.contentHashCode()
        result = 31 * result + sampleRate
        result = 31 * result + durationMs.hashCode()
        return result
    }
}

/**
 * Cross-platform audio recorder interface
 */
interface AudioRecorder {
    /**
     * Start recording audio
     * @param config Audio configuration
     * @return Result indicating success or failure
     */
    suspend fun startRecording(config: AudioConfig = AudioConfig()): Result<Unit>

    /**
     * Stop recording and get the recorded audio
     * @return Result containing audio data or error
     */
    suspend fun stopRecording(): Result<AudioRecordingResult>

    /**
     * Get current recording state
     * @return Current state as Flow
     */
    val recordingState: Flow<AudioRecordingState>

    /**
     * Check if currently recording
     * @return true if recording, false otherwise
     */
    fun isRecording(): Boolean

    /**
     * Release resources
     */
    fun release()
}

/**
 * Factory function to create platform-specific AudioRecorder
 */
expect fun createAudioRecorder(): AudioRecorder