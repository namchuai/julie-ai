package ai.julie.feature.modelmanagement.domain

import ai.julie.core.model.aimodel.LocalModel

fun interface StartModel {
    suspend fun startModel(threadId: String, model: LocalModel)
}