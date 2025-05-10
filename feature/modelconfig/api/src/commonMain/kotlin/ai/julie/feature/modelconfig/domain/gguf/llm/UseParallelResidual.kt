package ai.julie.feature.modelconfig.domain.gguf.llm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class UseParallelResidual(
    val architecture: SupportedArchitecture,
    val value: Boolean,
) {
    val key: String = "${architecture.value}.use_parallel_residual"
    
    /**
     * Whether or not the parallel residual logic should be used.
     */
}