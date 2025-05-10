package ai.julie.core.domain

import ai.julie.core.audio.AudioRecorder
import ai.julie.core.audio.createAudioRecorder
import ai.julie.core.data.whisper.WhisperModel
import ai.julie.logging.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Comprehensive use case that handles the entire voice input flow:
 * 1. Check/request microphone permissions
 * 2. Ensure whisper model is available
 * 3. Record audio
 * 4. Transcribe audio to text
 */
class RecordAndTranscribeUseCase(
    private val requestMicrophonePermissionUseCase: RequestMicrophonePermissionUseCase,
    private val initWhisperModelUseCase: InitWhisperModelUseCase,
    private val transcribeAudioUseCase: TranscribeAudioUseCase,
    private val downloadWhisperModelUseCase: DownloadWhisperModelUseCase,
    private val getWhisperModelsUseCase: GetWhisperModelsUseCase
) {

    private var audioRecorder: AudioRecorder? = null

    /**
     * Voice input state
     */
    sealed class VoiceInputState {
        object Idle : VoiceInputState()
        object CheckingPermissions : VoiceInputState()
        object PermissionDenied : VoiceInputState()
        object CheckingModel : VoiceInputState()
        data class DownloadingModel(val progress: Float) : VoiceInputState()
        object Recording : VoiceInputState()
        object Processing : VoiceInputState()
        data class Success(val transcription: String) : VoiceInputState()
        data class Error(val message: String, val cause: Throwable? = null) : VoiceInputState()
    }

    /**
     * Start voice input recording
     * @param model Optional whisper model to use (defaults to recommended)
     * @return Flow of voice input states
     */
    fun startVoiceInput(model: WhisperModel? = null): Flow<VoiceInputState> = flow {
        try {
            emit(VoiceInputState.Idle)

            // Step 1: Check microphone permissions
            emit(VoiceInputState.CheckingPermissions)
            val hasPermission = requestMicrophonePermissionUseCase.execute()

            Logger.d("Permission check result: ${hasPermission.getOrDefault(false)}")

            if (!hasPermission.getOrDefault(false)) {
                emit(VoiceInputState.PermissionDenied)
                return@flow
            }

            // Step 2: Ensure whisper model is available
            emit(VoiceInputState.CheckingModel)
            val whisperModel = model ?: getWhisperModelsUseCase.getRecommendedModel()

            // Check if model is downloaded
            if (!downloadWhisperModelUseCase.isModelDownloaded(whisperModel)) {
                Logger.d("Whisper model not found, downloading: ${whisperModel.name}")

                // Download model with progress
                downloadWhisperModelUseCase.execute(whisperModel).collect { progress ->
                    emit(VoiceInputState.DownloadingModel(progress.percentage))
                }
            }

            // Get model path
            val modelPath = downloadWhisperModelUseCase.getModelPath(whisperModel)
            if (modelPath == null) {
                emit(VoiceInputState.Error("Failed to get model path"))
                return@flow
            }

            // Initialize whisper model
            val initResult = initWhisperModelUseCase.execute(modelPath)
            if (initResult.isFailure) {
                emit(
                    VoiceInputState.Error(
                        "Failed to initialize whisper model",
                        initResult.exceptionOrNull()
                    )
                )
                return@flow
            }

            // Step 3: Start recording
            emit(VoiceInputState.Recording)
            audioRecorder = createAudioRecorder()
            val startResult = audioRecorder?.startRecording()

            if (startResult?.isFailure == true) {
                emit(
                    VoiceInputState.Error(
                        "Failed to start recording",
                        startResult.exceptionOrNull()
                    )
                )
                return@flow
            }

            Logger.d("Voice input recording started")

        } catch (e: Exception) {
//            Logger.e("Error in voice input flow", e)
            emit(VoiceInputState.Error("Voice input error", e))
        }
    }

    /**
     * Stop recording and transcribe
     * @return Flow of voice input states
     */
    fun stopVoiceInput(): Flow<VoiceInputState> = flow {
        try {
            val recorder = audioRecorder
            if (recorder == null) {
                emit(VoiceInputState.Error("No active recording"))
                return@flow
            }

            emit(VoiceInputState.Processing)

            // Stop recording and get audio data
            val recordingResult = recorder.stopRecording()
            if (recordingResult.isFailure) {
                emit(
                    VoiceInputState.Error(
                        "Failed to stop recording",
                        recordingResult.exceptionOrNull()
                    )
                )
                return@flow
            }

            val audioData = recordingResult.getOrNull()
            if (audioData == null) {
                emit(VoiceInputState.Error("No audio data recorded"))
                return@flow
            }

            Logger.d("Recorded ${audioData.audioData.size} audio samples, transcribing...")

            // Transcribe audio
            val transcriptionResult = transcribeAudioUseCase.execute(audioData.audioData)
            if (transcriptionResult.isSuccess) {
                val transcription = transcriptionResult.getOrNull() ?: ""
                emit(VoiceInputState.Success(transcription))
                Logger.d("Transcription completed: $transcription")
            } else {
                emit(
                    VoiceInputState.Error(
                        "Transcription failed",
                        transcriptionResult.exceptionOrNull()
                    )
                )
            }

        } catch (e: Exception) {
//            Logger.e("Error stopping voice input", e)
            emit(VoiceInputState.Error("Error processing audio", e))
        } finally {
            // Clean up
            audioRecorder?.release()
            audioRecorder = null
        }
    }

    /**
     * Cancel voice input
     */
    fun cancelVoiceInput() {
        audioRecorder?.release()
        audioRecorder = null
    }

    /**
     * Check if currently recording
     */
    fun isRecording(): Boolean {
        return audioRecorder?.isRecording() == true
    }

    /**
     * Open system settings for microphone permission
     */
    suspend fun openMicrophoneSettings() {
        requestMicrophonePermissionUseCase.openSettings()
    }
}