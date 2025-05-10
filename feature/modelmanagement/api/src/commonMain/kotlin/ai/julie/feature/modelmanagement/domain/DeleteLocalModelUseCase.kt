package ai.julie.feature.modelmanagement.domain

import ai.julie.feature.modelconfig.domain.DeleteModelContextParam
import ai.julie.feature.modelconfig.domain.DeleteModelLoadParam
import ai.julie.feature.modelconfig.domain.DeleteModelMetadata

class DeleteLocalModelUseCase(
    private val deleteLocalModel: DeleteLocalModel,
    private val deleteModelMetadata: DeleteModelMetadata,
    private val deleteModelContextParam: DeleteModelContextParam,
    private val deleteModelLoadParam: DeleteModelLoadParam,
) {

    suspend operator fun invoke(modelId: String) {
        // TODO: NamH we should stop model first

        // Delete model metadata first
        deleteModelMetadata.deleteModelMetadata(modelId)

        // Delete model settings
        deleteModelLoadParam.deleteModelLoadParam(modelId)
        deleteModelContextParam.deleteModelContextParam(modelId)

        // Then delete the model itself
        deleteLocalModel.deleteLocalModel(modelId)
    }
}