package ai.julie.feature.modelconfig.domain.modelcontextsetting

// corresponding to llama_context_params
data class ModelContextConfiguration(
    val contextLength: ContextLength? = null,
    val nBatches: NoOfBatch? = null,
)
