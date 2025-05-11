package ai.julie.feature.modelmanagement.screen.localmodelmanagement

import ai.julie.core.model.aimodel.AiModel

data class State(
    val models: List<AiModel> = emptyList(),
)