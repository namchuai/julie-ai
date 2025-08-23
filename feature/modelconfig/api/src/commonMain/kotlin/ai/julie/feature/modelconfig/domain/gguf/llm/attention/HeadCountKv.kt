package ai.julie.feature.modelconfig.domain.gguf.llm.attention

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class HeadCountKv(
    val architecture: SupportedArchitecture,
    val value: ULong,
) {
    val key: String = "${architecture.value}.attention.head_count_kv"
    
    /**
     * The number of heads per group used in Grouped-Query-Attention. 
     * If not present or if present and equal to `[llm].attention.head_count`, 
     * the model does not use GQA.
     */
}