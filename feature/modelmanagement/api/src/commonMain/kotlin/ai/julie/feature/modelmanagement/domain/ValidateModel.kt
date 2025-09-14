package ai.julie.feature.modelmanagement.domain

fun interface ValidateModel {
    suspend operator fun invoke(modelId: String)
}
