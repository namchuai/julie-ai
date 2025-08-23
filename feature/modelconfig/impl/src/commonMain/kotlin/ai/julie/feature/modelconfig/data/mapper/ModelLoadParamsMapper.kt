package ai.julie.feature.modelconfig.data.mapper

import ai.julie.core.model.ModelLoadParams
import kotbase.Document
import kotbase.MutableDocument
import kotlinx.datetime.Clock

/**
 * Convert ModelLoadParams to a Kotbase Document for storage
 */
fun ModelLoadParams.toDocument(modelId: String): MutableDocument {
    val currentTime = Clock.System.now().epochSeconds.toString()

    return MutableDocument(modelId).apply {
        setString("id", modelId)
        setInt("nGpuLayers", this@toDocument.nGpuLayers)
        setInt("mainGpu", this@toDocument.mainGpu)
        setBoolean("vocabOnly", this@toDocument.vocabOnly)
        setBoolean("useMmap", this@toDocument.useMmap)
        setBoolean("useMlock", this@toDocument.useMlock)
        setBoolean("checkTensors", this@toDocument.checkTensors)
        setString("createdAt", currentTime)
        setString("updatedAt", currentTime)
    }
}

/**
 * Convert a Kotbase Document to ModelLoadParams
 */
fun Document.toModelLoadParams(): ModelLoadParams {
    return ModelLoadParams(
        nGpuLayers = getInt("nGpuLayers") ?: 0,
        mainGpu = getInt("mainGpu") ?: 0,
        vocabOnly = getBoolean("vocabOnly") ?: false,
        useMmap = getBoolean("useMmap") ?: true,
        useMlock = getBoolean("useMlock") ?: false,
        checkTensors = getBoolean("checkTensors") ?: false
    )
}

