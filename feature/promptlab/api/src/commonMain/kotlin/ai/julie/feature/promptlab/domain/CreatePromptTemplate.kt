package ai.julie.feature.promptlab.domain

import ai.julie.feature.promptlab.model.PromptTemplate

fun interface CreatePromptTemplate {
    suspend fun createPromptTemplate(
        name: String,
        content: String
    ): PromptTemplate
}