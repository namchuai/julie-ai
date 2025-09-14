package ai.julie.feature.modelmanagement.data

import ai.julie.feature.modelmanagement.domain.FlowOfLocalModels
import ai.julie.feature.modelmanagement.domain.ValidateModel
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import kotlinx.coroutines.flow.first

class LocalModelValidator(
    private val flowOfLocalModels: FlowOfLocalModels,
) : ValidateModel {
    override suspend fun invoke(modelId: String) {
        val model = flowOfLocalModels.flowOfLocalModels().first().firstOrNull { it.id == modelId }
        requireNotNull(model) { "Model with id $modelId does not exist" }

        val localPath = model.localPath
        requireNotNull(localPath) { "Model $modelId has no local path configured" }

        val modelFile = PlatformFile(localPath)
        require(modelFile.exists()) { "Model file not found: $localPath" }
    }
}
