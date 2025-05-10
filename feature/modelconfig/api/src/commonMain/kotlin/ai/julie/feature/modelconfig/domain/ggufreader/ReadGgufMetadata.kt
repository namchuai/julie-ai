package ai.julie.feature.modelconfig.domain.ggufreader

import ai.julie.feature.modelconfig.domain.gguf.GgufMetadata
import io.github.vinceglb.filekit.PlatformFile

fun interface ReadGgufMetadata {

    suspend fun readGgufMetadata(file: PlatformFile): GgufMetadata
}
