package ai.julie.feature.modelmanagement.domain

fun interface StopAllModels {
    suspend fun stopAllModels()
}