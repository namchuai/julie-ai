package ai.julie.feature.promptlab.domain

import ai.julie.feature.promptlab.model.PromptTemplate
import kotlinx.coroutines.flow.Flow

fun interface FlowOfPromptTemplates {
    fun flowOfPromptTemplates(): Flow<List<PromptTemplate>>
}