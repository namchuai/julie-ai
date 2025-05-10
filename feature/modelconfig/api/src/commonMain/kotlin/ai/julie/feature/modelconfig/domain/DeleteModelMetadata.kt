package ai.julie.feature.modelconfig.domain

fun interface DeleteModelMetadata {
    suspend fun deleteModelMetadata(modelId: String)
}