package ai.julie.feature.modelconfig.domain.gguf.llm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class ExpertCount(
    val architecture: SupportedArchitecture,
    val value: UInt,
) {
    val key: String = "${architecture.value}.expert_count"
    
    /**
     * Number of experts in MoE models (optional for non-MoE arches).
     */
}