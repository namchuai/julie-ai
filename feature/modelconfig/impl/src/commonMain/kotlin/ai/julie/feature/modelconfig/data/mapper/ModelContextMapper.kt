package ai.julie.feature.modelconfig.data.mapper

import ai.julie.core.model.ModelContextParams
import kotbase.Document
import kotbase.MutableDocument
import kotlinx.datetime.Clock

/**
 * Convert ModelContextParams to a Kotbase Document for storage
 */
fun ModelContextParams.toDocument(modelId: String): MutableDocument {
    val currentTime = Clock.System.now().epochSeconds.toString()

    return MutableDocument(modelId).apply {
        setString("id", modelId)
        setInt("nCtx", this@toDocument.nCtx)
        setInt("nBatch", this@toDocument.nBatch)
        setInt("nUbatch", this@toDocument.nUbatch)
        setInt("nSeqMax", this@toDocument.nSeqMax)
        setInt("nThreads", this@toDocument.nThreads)
        setInt("nThreadsBatch", this@toDocument.nThreadsBatch)
        setFloat("ropeFreqBase", this@toDocument.ropeFreqBase)
        setFloat("ropeFreqScale", this@toDocument.ropeFreqScale)
        setBoolean("embeddings", this@toDocument.embeddings)
        setBoolean("offloadKqv", this@toDocument.offloadKqv)
        setBoolean("flashAttn", this@toDocument.flashAttn)
        setBoolean("noPerf", this@toDocument.noPerf)
        setString("createdAt", currentTime)
        setString("updatedAt", currentTime)
    }
}

/**
 * Convert a Kotbase Document to ModelContextParams
 */
fun Document.toModelContextParams(): ModelContextParams {
    return ModelContextParams(
        nCtx = getInt("nCtx") ?: 8192,
        nBatch = getInt("nBatch") ?: 16384,
        nUbatch = getInt("nUbatch") ?: 8192,
        nSeqMax = getInt("nSeqMax") ?: 1,
        nThreads = getInt("nThreads") ?: 0,
        nThreadsBatch = getInt("nThreadsBatch") ?: 0,
        ropeFreqBase = getFloat("ropeFreqBase") ?: 0.0f,
        ropeFreqScale = getFloat("ropeFreqScale") ?: 0.0f,
        embeddings = getBoolean("embeddings") ?: false,
        offloadKqv = getBoolean("offloadKqv") ?: true,
        flashAttn = getBoolean("flashAttn") ?: false,
        noPerf = getBoolean("noPerf") ?: true
    )
}

