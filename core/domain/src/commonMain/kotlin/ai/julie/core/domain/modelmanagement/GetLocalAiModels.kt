package ai.julie.core.domain.modelmanagement

import ai.julie.core.fs.FileSystem
import ai.julie.core.fs.model.FileType
import ai.julie.core.fs.model.ListFileOpts
import ai.julie.core.model.aimodel.LocalAiModel

class GetLocalAiModels(
    private val fileSystem: FileSystem,
) {
    suspend operator fun invoke(): List<LocalAiModel> {
        return fileSystem.listFiles(ListFileOpts(fileType = FileType.GgufModel)).map {
            LocalAiModel(
                id = it.name,
                title = it.name,
                description = it.name,
                localPath = it.absolutePath,
            )
        }
    }
}