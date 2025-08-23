package ai.julie.core.data.whisper

import ai.julie.logging.Logger
import ai.julie.whisperbinding.WhisperBinding
import ai.julie.whisperbinding.WhisperContext

class WhisperRepositoryImpl(
    private val whisperBinding: WhisperBinding
) : WhisperRepository {

    private var whisperContext: WhisperContext? = null
    private var isInitialized = false

    @Throws(IllegalStateException::class)
    override suspend fun initialize() {
        check(!isInitialized) { WHISPER_ALREADY_INITIALIZED_ERROR }

        try {
            // WhisperBinding doesn't need explicit initialization like LlamaBinding
            isInitialized = true
            Logger.d("WhisperRepository initialized successfully")
        } catch (e: Exception) {
//            Logger.e("Failed to initialize WhisperRepository", e)
            throw IllegalStateException("Failed to initialize whisper system", e)
        }
    }

    override suspend fun loadModel(modelPath: String): Result<Unit> {
        if (!isInitialized) {
            return Result.failure(IllegalStateException(WHISPER_NOT_INITIALIZED_ERROR))
        }

        return try {
            // Free existing context if any
            whisperContext?.let { context ->
                whisperBinding.freeContext(context)
            }

            // Load new model
            val context = whisperBinding.initContext(modelPath)
            if (context != null) {
                whisperContext = context
                Logger.d("Whisper model loaded successfully from: $modelPath")
                Result.success(Unit)
            } else {
                Logger.e("Failed to load whisper model from: $modelPath")
                Result.failure(IllegalStateException("Failed to load whisper model"))
            }
        } catch (e: Exception) {
//            Logger.e("Exception while loading whisper model", e)
            Result.failure(e)
        }
    }

    override suspend fun transcribe(audioData: FloatArray): Result<String> {
        if (!isInitialized) {
            return Result.failure(IllegalStateException(WHISPER_NOT_INITIALIZED_ERROR))
        }

        val context = whisperContext
        if (context == null) {
            return Result.failure(IllegalStateException(WHISPER_MODEL_NOT_LOADED_ERROR))
        }

        return try {
            val transcription = whisperBinding.transcribe(context, audioData)

            if (transcription.startsWith("Error:")) {
                Logger.e("Whisper transcription failed: $transcription")
                Result.failure(IllegalStateException(transcription))
            } else {
                Logger.d("Whisper transcription successful: ${transcription.take(50)}...")
                Result.success(transcription.trim())
            }
        } catch (e: Exception) {
//            Logger.e("Exception during whisper transcription", e)
            Result.failure(e)
        }
    }

    override fun isInitialized(): Boolean = isInitialized

    override fun isModelLoaded(): Boolean = whisperContext != null

    override fun getVersion(): String {
        return if (isInitialized) {
            whisperBinding.getVersion()
        } else {
            "Not initialized"
        }
    }

    override suspend fun cleanUp() {
        try {
            whisperContext?.let { context ->
                whisperBinding.freeContext(context)
                whisperContext = null
                Logger.d("Whisper context freed")
            }
            isInitialized = false
            Logger.d("WhisperRepository cleaned up")
        } catch (e: Exception) {
//            Logger.e("Error during whisper cleanup", e)
        }
    }

    companion object {
        private const val WHISPER_ALREADY_INITIALIZED_ERROR = "Whisper is already initialized"
        private const val WHISPER_NOT_INITIALIZED_ERROR = "Whisper is not initialized"
        private const val WHISPER_MODEL_NOT_LOADED_ERROR = "Whisper model is not loaded"
    }
}