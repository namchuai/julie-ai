package ai.julie.feature.modelmanagement.ui.localmodelmanagement

import ai.julie.core.model.aimodel.LocalModel

data class LocalModelManagementState(
    val models: List<LocalModel> = emptyList(),
)
