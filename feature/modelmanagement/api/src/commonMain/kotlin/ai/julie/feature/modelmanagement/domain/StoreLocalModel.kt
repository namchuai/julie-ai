package ai.julie.feature.modelmanagement.domain

import ai.julie.core.model.aimodel.LocalModel

fun interface StoreLocalModel {
    suspend fun storeLocalModel(model: LocalModel)
}