package ai.julie.feature.modelconfig.domain.gguf.llm.scaling

import ai.julie.feature.modelconfig.domain.gguf.general.SupportedArchitecture

enum class RopeScalingType(val value: String) {
    NONE("none"),
    LINEAR("linear"),
    YARN("yarn")
}

data class Type(
    val architecture: SupportedArchitecture,
    val value: RopeScalingType,
) {
    val key: String = "${architecture.value}.rope.scaling.type"
    
    /**
     * Can be `none`, `linear`, or `yarn`.
     */
}