package ai.julie.core.domain.modelmanagement

import ai.julie.core.fs.FileSystem
import ai.julie.core.fs.types.FileType
import ai.julie.core.fs.types.ListFileOpts

class GetDownloadedGgufFiles(
    private val fileSystem: FileSystem,
) {
    suspend operator fun invoke(): List<String> {
        return fileSystem.listFiles(ListFileOpts(fileType = FileType.GgufModel))
    }
}