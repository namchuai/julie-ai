package ai.julie.feature.modelconfig.domain.gguf.llm.ssm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class InnerSize(
    val architecture: SupportedArchitecture,
    val value: UInt,
) {
    val key: String = "${architecture.value}.ssm.inner_size"
    
    /**
     * The embedding size of the states.
     */
}