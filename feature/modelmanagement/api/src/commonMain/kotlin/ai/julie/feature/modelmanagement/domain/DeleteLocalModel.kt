package ai.julie.feature.modelmanagement.domain

fun interface DeleteLocalModel {
    suspend fun deleteLocalModel(modelId: String)
}