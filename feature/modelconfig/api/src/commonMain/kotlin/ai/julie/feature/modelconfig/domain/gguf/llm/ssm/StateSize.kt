package ai.julie.feature.modelconfig.domain.gguf.llm.ssm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class StateSize(
    val architecture: SupportedArchitecture,
    val value: UInt,
) {
    val key: String = "${architecture.value}.ssm.state_size"
    
    /**
     * The size of the recurrent state.
     */
}