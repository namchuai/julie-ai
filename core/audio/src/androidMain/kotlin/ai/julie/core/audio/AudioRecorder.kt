package ai.julie.core.audio

import ai.julie.logging.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

actual fun createAudioRecorder(): AudioRecorder = AndroidAudioRecorder()

class AndroidAudioRecorder : AudioRecorder {
    private val _recordingState = MutableStateFlow<AudioRecordingState>(AudioRecordingState.Idle)
    override val recordingState: Flow<AudioRecordingState> = _recordingState.asStateFlow()

    override suspend fun startRecording(config: AudioConfig): Result<Unit> {
        Logger.w("Android audio recording not implemented yet")
        _recordingState.value = AudioRecordingState.Error("Android audio recording not implemented")
        return Result.failure(UnsupportedOperationException("Android audio recording not implemented"))
    }

    override suspend fun stopRecording(): Result<AudioRecordingResult> {
        Logger.w("Android audio recording not implemented yet")
        return Result.failure(UnsupportedOperationException("Android audio recording not implemented"))
    }

    override fun isRecording(): Boolean = false

    override fun release() {
        // Nothing to release
    }
}