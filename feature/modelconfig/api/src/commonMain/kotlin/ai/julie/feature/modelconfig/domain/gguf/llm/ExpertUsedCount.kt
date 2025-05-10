package ai.julie.feature.modelconfig.domain.gguf.llm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class ExpertUsedCount(
    val architecture: SupportedArchitecture,
    val value: UInt,
) {
    val key: String = "${architecture.value}.expert_used_count"
    
    /**
     * Number of experts used during each token token evaluation (optional for non-MoE arches).
     */
}