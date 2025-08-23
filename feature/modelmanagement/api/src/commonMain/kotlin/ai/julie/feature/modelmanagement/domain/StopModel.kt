package ai.julie.feature.modelmanagement.domain

fun interface StopModel {
    suspend fun stopModel(modelId: String)
}