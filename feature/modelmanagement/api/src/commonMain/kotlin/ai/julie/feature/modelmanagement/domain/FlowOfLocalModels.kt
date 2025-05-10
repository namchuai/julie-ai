package ai.julie.feature.modelmanagement.domain

import ai.julie.core.model.aimodel.LocalModel
import kotlinx.coroutines.flow.Flow

fun interface FlowOfLocalModels {
    fun flowOfLocalModels(): Flow<List<LocalModel>>
}