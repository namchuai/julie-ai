package ai.julie.ui.bottomstatusbar

import ai.julie.core.model.aimodel.LocalModel

data class BottomStatusBarState(
    val runningModels: Set<LocalModel> = emptySet(),
    val startingModels: Map<LocalModel, Float> = emptyMap(),
    val appVersion: String,
    val buildNo: String,
)