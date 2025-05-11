package ai.julie.llamabinding.model

import ai.julie.core.model.ModelContextParams

data class LlamaContextParams(
    val nCtx: Int = 512,
    val nBatch: Int = 2048, // Default from C++
    val nUbatch: Int = 512,
    val nSeqMax: Int = 1,
    val nThreads: Int = 0, // Default 0 allows auto-detection by library
    val nThreadsBatch: Int = 0, // Default 0 allows auto-detection by library
    val ropeFreqBase: Float = 0.0f,
    val ropeFreqScale: Float = 0.0f,
    val embeddings: Boolean = false,
    val offloadKqv: Boolean = true,
    val flashAttn: Boolean = false,
    val noPerf: Boolean = true // Default from C++
) {

    companion object {
        fun from(params: ModelContextParams): LlamaContextParams {
            return LlamaContextParams(
                nCtx = params.nCtx,
                nBatch = params.nBatch,
                nUbatch = params.nUbatch,
                nSeqMax = params.nSeqMax,
                nThreads = params.nThreads,
                nThreadsBatch = params.nThreadsBatch,
                ropeFreqBase = params.ropeFreqBase,
                ropeFreqScale = params.ropeFreqScale,
                embeddings = params.embeddings,
                offloadKqv = params.offloadKqv,
                flashAttn = params.flashAttn,
                noPerf = params.noPerf
            )
        }
    }
}
