package ai.julie.feature.promptlab.domain

import ai.julie.feature.promptlab.model.PromptTemplateContext

fun interface UpdatePromptTemplateContext {
    suspend fun updatePromptTemplateContext(context: PromptTemplateContext): PromptTemplateContext
}