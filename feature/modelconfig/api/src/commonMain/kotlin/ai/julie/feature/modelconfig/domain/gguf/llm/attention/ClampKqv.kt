package ai.julie.feature.modelconfig.domain.gguf.llm.attention

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class ClampKqv(
    val architecture: SupportedArchitecture,
    val value: Float,
) {
    val key: String = "${architecture.value}.attention.clamp_kqv"
    
    /**
     * Value (`C`) to clamp the values of the `Q`, `K`, and `V` tensors between (`[-C, C]`).
     */
}