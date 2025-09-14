package ai.julie.feature.promptlab.domain

import ai.julie.feature.promptlab.model.PromptTemplateContext
import kotlinx.coroutines.flow.Flow

fun interface FlowOfPromptTemplateContexts {
    fun flowOfPromptTemplateContexts(promptTemplateId: String): Flow<List<PromptTemplateContext>>
}