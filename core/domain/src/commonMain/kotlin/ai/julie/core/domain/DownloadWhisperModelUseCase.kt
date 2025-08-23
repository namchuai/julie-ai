package ai.julie.core.domain

import ai.julie.core.data.whisper.DownloadProgress
import ai.julie.core.data.whisper.WhisperModel
import ai.julie.core.data.whisper.WhisperModelRepository
import ai.julie.logging.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class DownloadWhisperModelUseCase(
    private val whisperModelRepository: WhisperModelRepository
) {
    /**
     * Download a whisper model
     * @param model The whisper model to download
     * @return Flow of download progress
     */
    fun execute(model: WhisperModel): Flow<DownloadProgress> {
        return whisperModelRepository.downloadModel(model)
            .onStart {
                Logger.d("Starting download of whisper model: ${model.name}")
            }
            .onCompletion { exception ->
                if (exception == null) {
                    Logger.d("Successfully downloaded whisper model: ${model.name}")
                } else {
//                    Logger.e("Failed to download whisper model: ${model.name}", exception)
                }
            }
            .catch { exception ->
//                Logger.e("Download error for whisper model: ${model.name}", exception)
                throw exception
            }
    }

    /**
     * Check if a model is already downloaded
     * @param model The whisper model to check
     * @return true if model is downloaded, false otherwise
     */
    suspend fun isModelDownloaded(model: WhisperModel): Boolean {
        return whisperModelRepository.isModelDownloaded(model)
    }

    /**
     * Get the local path to a downloaded model
     * @param model The whisper model
     * @return Local path if model exists, null otherwise
     */
    suspend fun getModelPath(model: WhisperModel): String? {
        return whisperModelRepository.getModelPath(model)
    }
}