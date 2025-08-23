package ai.julie.core.model

// TODO: NamH rework this class
data class ModelContextParams(
    val nCtx: Int = 8192,  // Increased from 512 to 8K tokens
    val nBatch: Int = 16384,  // Increased to handle larger prompts
    val nUbatch: Int = 8192,
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