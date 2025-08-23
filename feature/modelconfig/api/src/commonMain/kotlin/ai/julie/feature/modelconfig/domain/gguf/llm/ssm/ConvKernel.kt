package ai.julie.feature.modelconfig.domain.gguf.llm.ssm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class ConvKernel(
    val architecture: SupportedArchitecture,
    val value: UInt,
) {
    val key: String = "${architecture.value}.ssm.conv_kernel"
    
    /**
     * The size of the rolling/shift state.
     */
}