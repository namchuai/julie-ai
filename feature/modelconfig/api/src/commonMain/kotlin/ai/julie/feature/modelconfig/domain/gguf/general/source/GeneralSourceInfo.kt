package ai.julie.feature.modelconfig.domain.gguf.general.source

sealed interface GeneralSourceInfo {
    val key: String
    val value: Any
}
