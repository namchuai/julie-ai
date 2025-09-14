package ai.julie.feature.promptlab.domain

import ai.julie.feature.promptlab.model.PromptTemplate

fun interface UpdatePromptTemplate {
    suspend fun updatePromptTemplate(template: PromptTemplate): PromptTemplate
}