package ai.julie.feature.modelmanagement.domain

import ai.julie.core.model.aimodel.LocalModel
import ai.julie.feature.modelconfig.domain.StoreModelMetadata
import ai.julie.feature.modelconfig.domain.ggufreader.ReadGgufMetadata
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.path
import kotlin.uuid.Uuid

class AddLocalModelFromFileUseCase(
    private val storeLocalModel: StoreLocalModel,
    private val storeModelMetadata: StoreModelMetadata,
    private val readGguf: ReadGgufMetadata,
) {

    suspend operator fun invoke(file: PlatformFile) {
        val ggufMetadata = readGguf.readGgufMetadata(file)
        println("Gguf metadata: $ggufMetadata")

        val modelId = Uuid.random().toString()
        val fileName = file.name
        val modelTitle = ggufMetadata.name?.value?.takeIf { it.isNotBlank() }
            ?: fileName.substringBeforeLast('.')
        val modelDescription = ggufMetadata.description?.value?.takeIf { it.isNotBlank() }
            ?: "Model loaded from $fileName"

        val localModel = LocalModel(
            id = modelId,
            title = modelTitle,
            description = modelDescription,
            localPath = file.path,
        )

        storeModelMetadata.storeModelMetadata(modelId, ggufMetadata)
        storeLocalModel.storeLocalModel(localModel)
    }
}
