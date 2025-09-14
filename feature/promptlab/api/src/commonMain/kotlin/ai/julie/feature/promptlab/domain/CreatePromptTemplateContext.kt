package ai.julie.feature.promptlab.domain

import ai.julie.feature.promptlab.model.PromptTemplateContext

fun interface CreatePromptTemplateContext {
    suspend fun createPromptTemplateContext(
        promptTemplateId: String,
        variables: Map<String, String>
    ): PromptTemplateContext
}