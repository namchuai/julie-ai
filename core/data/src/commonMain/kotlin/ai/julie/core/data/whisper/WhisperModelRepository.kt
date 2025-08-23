package ai.julie.core.data.whisper

import ai.julie.core.network.FileDownloader
import ai.julie.logging.Logger
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Download progress information
 */
data class DownloadProgress(
    val downloadedBytes: Long,
    val totalBytes: Long,
    val percentage: Float = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes) * 100f else 0f
)

interface WhisperModelRepository {
    /**
     * Get list of available whisper models
     */
    fun getAvailableModels(): List<WhisperModel>

    /**
     * Get list of downloaded models
     */
    suspend fun getDownloadedModels(): List<WhisperModel>

    /**
     * Check if a model is downloaded
     */
    suspend fun isModelDownloaded(model: WhisperModel): Boolean

    /**
     * Download a whisper model
     */
    fun downloadModel(model: WhisperModel): Flow<DownloadProgress>

    /**
     * Get the local path to a downloaded model
     */
    suspend fun getModelPath(model: WhisperModel): String?

    /**
     * Delete a downloaded model
     */
    suspend fun deleteModel(model: WhisperModel): Result<Unit>

    /**
     * Get the default model directory
     */
    fun getModelDirectory(): String
}

class WhisperModelRepositoryImpl(
    private val baseDirectory: String,
    private val fileDownloader: FileDownloader
) : WhisperModelRepository {

    private val modelDirectory = "whisper-models"

    override fun getAvailableModels(): List<WhisperModel> {
        return WhisperModel.AVAILABLE_MODELS
    }

    override suspend fun getDownloadedModels(): List<WhisperModel> {
        return getAvailableModels().filter { model ->
            val modelFile = PlatformFile("$baseDirectory/$modelDirectory/${model.filename}")
            modelFile.exists()
        }
    }

    override suspend fun isModelDownloaded(model: WhisperModel): Boolean {
        val modelFile = PlatformFile("$baseDirectory/$modelDirectory/${model.filename}")
        return modelFile.exists()
    }

    override fun downloadModel(model: WhisperModel): Flow<DownloadProgress> = flow {
        try {
            val modelFile = PlatformFile("$baseDirectory/$modelDirectory/${model.filename}")
            
            // Use the FileDownloader that returns progress flow
            fileDownloader.downloadFile(model.downloadUrl, modelFile).collect { progress ->
                emit(DownloadProgress(
                    downloadedBytes = progress.downloadedBytes,
                    totalBytes = progress.totalBytes,
                    percentage = progress.percentage
                ))
            }
            
            Logger.d("Downloaded whisper model: ${model.name}")
        } catch (e: Exception) {
            Logger.e("Error downloading whisper model: ${model.name} - ${e.message}")
            throw e
        }
    }

    override suspend fun getModelPath(model: WhisperModel): String? {
        val modelFile = PlatformFile("$baseDirectory/$modelDirectory/${model.filename}")
        return if (modelFile.exists()) {
            modelFile.path
        } else {
            null
        }
    }

    override suspend fun deleteModel(model: WhisperModel): Result<Unit> {
        return try {
            val modelFile = PlatformFile("$baseDirectory/$modelDirectory/${model.filename}")
            if (modelFile.exists()) {
                modelFile.delete()
                Logger.d("Deleted whisper model: ${model.name}")
                Result.success(Unit)
            } else {
                Result.success(Unit) // Already deleted
            }
        } catch (e: Exception) {
            Logger.e("Error deleting whisper model: ${model.name} - ${e.message}")
            Result.failure(e)
        }
    }

    override fun getModelDirectory(): String {
        return "$baseDirectory/$modelDirectory"
    }
}