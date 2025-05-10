package ai.julie.feature.modelconfig.domain.gguf.general

sealed interface GeneralInfo {
    val key: String

    val value: Any
}
