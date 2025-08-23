package ai.julie.core.domain

import ai.julie.core.data.whisper.WhisperModel
import ai.julie.core.data.whisper.WhisperModelRepository
import ai.julie.logging.Logger

class GetWhisperModelsUseCase(
    private val whisperModelRepository: WhisperModelRepository
) {
    /**
     * Get all available whisper models
     * @return List of available whisper models
     */
    fun getAvailableModels(): List<WhisperModel> {
        return whisperModelRepository.getAvailableModels()
    }

    /**
     * Get downloaded whisper models
     * @return List of downloaded whisper models
     */
    suspend fun getDownloadedModels(): List<WhisperModel> {
        return try {
            whisperModelRepository.getDownloadedModels()
        } catch (e: Exception) {
//            Logger.e("Failed to get downloaded whisper models", e)
            emptyList()
        }
    }

    /**
     * Get the recommended whisper model for new users
     * @return Recommended whisper model
     */
    fun getRecommendedModel(): WhisperModel {
        return WhisperModel.getRecommended()
    }

    /**
     * Get a whisper model by ID
     * @param modelId The model ID
     * @return WhisperModel if found, null otherwise
     */
    fun getModelById(modelId: String): WhisperModel? {
        return WhisperModel.getById(modelId)
    }

    /**
     * Delete a downloaded whisper model
     * @param model The whisper model to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteModel(model: WhisperModel): Result<Unit> {
        return try {
            val result = whisperModelRepository.deleteModel(model)
            if (result.isSuccess) {
                Logger.d("Successfully deleted whisper model: ${model.name}")
            } else {
                Logger.e("Failed to delete whisper model: ${model.name}")
            }
            result
        } catch (e: Exception) {
//            Logger.e("Error deleting whisper model: ${model.name}", e)
            Result.failure(e)
        }
    }
}