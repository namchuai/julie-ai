package ai.julie.core.model

data class ModelContextParams(
    val nCtx: Int = 512,
    val nBatch: Int = 2048,
    val nUbatch: Int = 512,
    val nSeqMax: Int = 1,
    val nThreads: Int = 0,
    val nThreadsBatch: Int = 0,
    val ropeFreqBase: Float = 0.0f,
    val ropeFreqScale: Float = 0.0f,
    val embeddings: Boolean = false,
    val offloadKqv: Boolean = true,
    val flashAttn: Boolean = false,
    val noPerf: Boolean = true
)