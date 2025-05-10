package ai.julie.feature.modelconfig.domain.gguf.llm

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

data class ArchitectureVersion(
    val architecture: SupportedArchitecture,
    val value: UInt,
) {
    val key: String = "${architecture.value}.architecture_version"
    
    /**
     * The architecture version. For RWKV, the only allowed value currently is 4. 
     * Version 5 is expected to appear some time in the future.
     */
}