package ai.julie.feature.modelmanagement.domain

import ai.julie.core.model.aimodel.LocalModel

interface ReloadModel {
    suspend fun reloadModel(threadId: String, model: LocalModel)
}