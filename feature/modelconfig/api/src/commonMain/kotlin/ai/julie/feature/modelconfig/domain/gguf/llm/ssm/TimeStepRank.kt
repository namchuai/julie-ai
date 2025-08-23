package ai.julie.feature.modelconfig.domain.gguf.llm.ssm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class TimeStepRank(
    val architecture: SupportedArchitecture,
    val value: UInt,
) {
    val key: String = "${architecture.value}.ssm.time_step_rank"
    
    /**
     * The rank of time steps.
     */
}