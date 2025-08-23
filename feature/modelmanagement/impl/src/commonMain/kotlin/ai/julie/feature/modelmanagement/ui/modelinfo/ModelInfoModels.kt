package ai.julie.feature.modelmanagement.ui.modelinfo

data class ModelInfoState(
    val selectedTab: ModelInfoTab,
)

enum class ModelInfoTab {
    MODEL_SETTINGS,
    MODEL_METADATA,
}