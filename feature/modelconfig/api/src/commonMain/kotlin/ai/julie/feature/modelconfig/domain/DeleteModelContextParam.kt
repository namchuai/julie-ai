package ai.julie.feature.modelconfig.domain

// should be used only when deleting model
fun interface DeleteModelContextParam {
    suspend fun deleteModelContextParam(modelId: String)
}