package ai.julie.feature.modelconfig.domain.setting

sealed interface ModelSettingItem {
    val key: String

    val value: Any
}
