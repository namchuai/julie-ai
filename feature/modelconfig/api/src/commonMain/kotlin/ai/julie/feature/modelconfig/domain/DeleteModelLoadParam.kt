package ai.julie.feature.modelconfig.domain

fun interface DeleteModelLoadParam {
    suspend fun deleteModelLoadParam(modelId: String)
}